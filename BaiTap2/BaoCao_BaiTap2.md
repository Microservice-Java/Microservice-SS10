# BÁO CÁO BÀI TẬP 2: CHUYỂN ĐỔI RESTTEMPLATE SANG FEIGNCLIENT

---

## 1. BẢNG SO SÁNH SỐ DÒNG CODE: RESTTEMPLATE VS FEIGNCLIENT

| Tiêu chí so sánh | ProductServiceClientRT (RestTemplate) | ProductClient (FeignClient) |
| :--- | :--- | :--- |
| **Loại mã nguồn (Code Style)** | Class Imperative (Thủ công) | Declarative Interface (Khai báo) |
| **Số dòng code (LoC)** | **~38 dòng code Java** | **~14 dòng code Java** (Giảm **>60%**) |
| **Xây dựng URL & Query Params** | Phải ghép chuỗi URL thủ công (`http://product-service/api/...`) | Dùng Annotation chuẩn Spring MVC (`@GetMapping("/api/products/{id}")`) |
| **Xử lý Response / Parse Object** | Gọi `restTemplate.getForObject(...)` thủ công | Tự động parse response body thành Object / List |
| **Tích hợp LoadBalancer & Eureka** | Cần cấu hình Bean `@LoadBalanced RestTemplate` | Tự động tích hợp sẵn qua `@FeignClient(name = "product-service")` |
| **Xử lý Exception & Fallback** | Bắt khối `try-catch` lặp đi lặp lại ở từng method | Tự động điều hướng qua `FallbackFactory` tập trung khi gặp sự cố |

---

## 2. PHÂN TÍCH ƯU / NHƯỢC ĐIỂM CỦA 2 CÁCH TIẾP CẬN

### 🟢 OpenFeign (FeignClient)

#### Ưu điểm:
1. **Code Declarative ngắn gọn, sạch sẻ**: Chỉ cần khai báo `interface` và annotation, không cần viết class implementation.
2. **Quản lý Fallback tập trung**: Thông qua `FallbackFactory`, toàn bộ logic dự phòng và log ngoại lệ được tách biệt khỏi business logic chính.
3. **Đồng bộ Annotation Spring MVC**: Sử dụng chung `@GetMapping`, `@PostMapping`, `@PathVariable`, `@RequestBody` giống hệt Controller.
4. **Tích hợp sẵn Spring Cloud**: Tự động liên kết Eureka Service Discovery, LoadBalancer, Circuit Breaker và Timeout configuration qua `application.yml`.

#### Nhược điểm:
1. **Trừu tượng hóa cao (Abstraction Level)**: Khó debug trực tiếp luồng HTTP bên dưới nếu chưa quen với Feign Interceptor.
2. **Khó tùy biến các Request phức tạp đặc thù**: Khi cần can thiệp sâu vào từng byte stream hoặc custom HTTP headers động theo từng socket connection, Feign cần thêm cấu hình encoder/decoder phức tạp hơn.

---

### 🟡 RestTemplate

#### Ưu điểm:
1. **Kiểm soát chi tiết (Low-level Control)**: Trực tiếp can thiệp vào các thành phần Request/Response, Custom Header, Cookie, và Proxy.
2. **Linh hoạt cho các API bên ngoài (External APIs)**: Rất phù hợp khi gọi các dịch vụ bên thứ 3 không theo chuẩn Naming Convention của Spring Cloud Eureka.

#### Nhược điểm:
1. **Boilerplate Code dài dòng**: Phải tự viết `try-catch`, bóc tách `ResponseEntity`, xử lý mảng -> List thủ công.
2. **Dễ mắc lỗi Hardcode & Quên Timeout**: Giống như sự cố trong Bài tập 1 của thực tập sinh.

---

## 3. KHI NÀO NÊN DÙNG CÁCH NÀO? (DECISION CRITERIA)

- **Dùng FeignClient khi**:
  - Giao tiếp đồng bộ giữa các Microservices nội bộ trong cùng hệ thống (Service-to-Service communication).
  - Dịch vụ đã đăng ký trên Eureka Discovery Server.
  - Muốn tối ưu hóa thời gian phát triển, tăng độ sạch của mã nguồn và tách biệt logic Fallback/CircuitBreaker.

- **Dùng RestTemplate (hoặc WebClient) khi**:
  - Gọi các REST API của bên thứ 3 ngoài hệ thống (Third-party External APIs như VNPay, Momo, Google API, SendGrid...).
  - Cần tùy chỉnh sâu các cơ chế HTTP client đặc thù mà Feign Interface không hỗ trợ trực tiếp.
  - Hệ thống Legacy chưa chuyển sang Spring Cloud Ecosystem.

---

## 4. KẾT QUẢ UNIT TEST (JUnit 5 + Mockito)

Tất cả **4 Test Cases** trong `ProductClientTest.java` đã chạy **PASS 100%**:

```text
ProductClientTest > Test 1: Feign Client ProductClient.getById thành công PASSED
ProductClientTest > Test 2: Feign FallbackFactory kích hoạt khi có sự cố getById PASSED
ProductClientTest > Test 3: Feign FallbackFactory kích hoạt cho getAll() - Trả danh sách rỗng PASSED
ProductClientTest > Test 4: UserClient.getUserById thành công PASSED

BUILD SUCCESSFUL in 15s
```
