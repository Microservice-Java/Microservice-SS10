# BÁO CÁO BÀI TẬP 3: CHUYỂN ĐỔI RESTTEMPLATE SANG WEBCLIENT TRONG SPRING WEBFLUX

---

## 1. PHÂN TÍCH LỖI NGHIÊM TRỌNG TRONG CODE CŨ

### 🔴 Lỗi 1: Sử dụng Synchronous / Blocking Client (`RestTemplate`) trong môi trường Reactive (`Spring WebFlux`)
* **Nguyên nhân**: Thực tập sinh sử dụng `RestTemplate.getForObject(...)` để thực hiện HTTP request đồng bộ bên trong một Spring WebFlux application.
* **Cơ chế gây sự cố cạn kiệt Thread (Thread Starvation)**:
  - Spring WebFlux chạy trên máy chủ **Netty (Event Loop Model)** với số lượng worker thread rất ít (thường bằng số CPU Cores, ví dụ: 4 hoặc 8 threads) thay vì Servlet Pool 200 threads như Tomcat.
  - Khi `RestTemplate` thực hiện yêu cầu HTTP đồng bộ, nó sẽ **chiếm giữ và Block vĩnh viễn (gợi nén Socket)** 1 Event Loop thread cho đến khi phản hồi được nhận về.
  - Khi có **10.000 request truy cập cùng lúc** dịp Flash Sale, tất cả 4-8 Netty Event Loop threads đều bị chiếm giữ để chờ I/O HTTP. Netty không còn thread nào để tiếp nhận (accept) request mới từ người dùng.
  - Hậu quả: Hệ thống nghẽn hoàn toàn, báo lỗi `HTTP 500 Internal Server Error`, rớt kết nối và treo cứng (Server Crash).

---

### 🔴 Lỗi 2: Trả về kiểu dữ liệu đồng bộ (`Banner`) thay vì Kiều dữ liệu Reactive (`Mono<Banner>`)
* **Nguyên nhân**: Hàm `public Banner getActiveBanner()` chặn luồng xử lý và trả về giá trị trực tiếp.
* **Hậu quả**: Không áp dụng được mô hình Reactive Streams (Publisher - Subscriber Pattern). Khung làm việc WebFlux không thể xếp lịch (schedule) công việc một cách không bất đồng bộ (Asynchronous).

---

### 🔴 Lỗi 3: Không có Timeout và Fallback handling khi service đích gặp sự cố
* **Nguyên nhân**: Ném ra `new RuntimeException("Promotion service unavailable")` khiến HTTP Response trả về lỗi `500 Server Error` làm crash trải nghiệm trang chủ người dùng.

---

## 2. NGUYÊN TẮC VÀ CÁCH KHẮC PHỦC

1. **Sử dụng `WebClient` chuẩn Non-Blocking I/O**:
   - `WebClient` tận dụng Reactor Netty Client để gửi HTTP request theo cơ chế Event-driven asynchronous, không làm nghẽn Event Loop Thread.

2. **Trả về `Mono<Banner>`**:
   - Chuyển đổi phương thức trả về kiểu dữ liệu Reactive `Mono<Banner>`.

3. **Cấu hình Timeout 2s & Fallback `onErrorResume`**:
   - Áp dụng `.timeout(Duration.ofSeconds(2))` trên Reactor Publisher.
   - Khi quá 2s hoặc `promotion-service` sự cố, hàm `.onErrorResume(...)` bắt lỗi và trả về Banner mặc định mà không ném ngoại lệ hay làm crash ứng dụng.

---

## 3. KẾT QUẢ UNIT TEST (JUnit 5 + Project Reactor StepVerifier)

Tất cả **2 Test Cases** trong `PromotionServiceTest.java` đã chạy **PASS 100%**:

```text
PromotionServiceTest > Test 1: Gọi WebClient thành công - Trả về Active Banner chuẩn PASSED
PromotionServiceTest > Test 2: Mô phỏng Timeout (2s) - Trả về Fallback Banner mặc định PASSED

BUILD SUCCESSFUL in 23s
```
