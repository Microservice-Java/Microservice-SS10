# BÁO CÁO BÀI TẬP 1: SỬA LỖI HARDCODE URL TRONG RESTTEMPLATE CỦA ORDER-SERVICE

---

## 1. PHÂN TÍCH 3 LỖI TRONG CODE CŨ & HẬU QUẢ PRODUCTION

### 🔴 Lỗi 1: Khởi tạo trực tiếp `new RestTemplate()` không có `@LoadBalanced`
* **Nguyên nhân**: Thực tập sinh tự khởi tạo `new RestTemplate()` trong component thay vì sử dụng RestTemplate Bean được quản lý bởi Spring Container.
* **Hậu quả Production**:
  - RestTemplate không qua Spring Cloud LoadBalancer / Eureka Client interceptor.
  - Không thể giải mã `service-id` thành IP:Port thực tế (khi gọi `http://product-service/...` sẽ bị ném ra `UnknownHostException`).
  - Không thể cân bằng tải (Load Balancing) giữa các instance của `product-service`.

---

### 🔴 Lỗi 2: Hardcode IP cứng và Cổng cố định (`http://192.168.1.45:8082/...`)
* **Nguyên nhân**: Nhúng trực tiếp địa chỉ IP và Port của môi trường dev vào source code (`String url = "http://192.168.1.45:8082/api/products/{id}"`).
* **Hậu quả Production**:
  - Code thất bại ngay lập tức khi deploy lên môi trường Staging/Production vì IP/Port server sẽ thay đổi.
  - Vi phạm nguyên tắc Microservices: Các service phải linh hoạt và độc lập vị trí địa lý.
  - Khi instance tại IP `192.168.1.45` bị quá tải hoặc crash, toàn bộ yêu cầu sẽ thất bại dù có hàng chục instance khác đang rảnh rỗi.

---

### 🔴 Lỗi 3: Không cấu hình Timeout (`connectTimeout` & `readTimeout`)
* **Nguyên nhân**: Mặc định `RestTemplate` không giới hạn thời gian chờ kết nối và đọc dữ liệu (Infinite Timeout).
* **Hậu quả Production**:
  - Khi `product-service` gặp sự cố (bị đơ, quá tải, hoặc nghẽn mạng), các Request Thread của `order-service` gọi sang sẽ bị **treo vĩnh viễn (Blocked)**.
  - Gây ra hiện tượng **Thread Starvation (Cạn kiệt Thread)** trên Tomcat server của `order-service`.
  - Dẫn đến hiệu ứng **Cascading Failure (Lỗi dây chuyền / Nghẽn cổ chai)**: Sự cố ở một service nhỏ làm nổ tung cả hệ thống `order-service` và API Gateway.

---

## 2. NGUYÊN TẮC VÀ CÁCH KHẮC PHỦC

1. **Cấu hình `@LoadBalanced RestTemplate` Bean**:
   - Sử dụng `RestTemplateBuilder` thiết lập `connectTimeout = 2s` và `readTimeout = 3s`.
   - Đánh dấu `@LoadBalanced` để Spring Cloud tự động giải mã `service-id` qua Eureka Server.

2. **Sử dụng Virtual Hostname (Service ID)**:
   - Thay thế IP cứng bằng `http://product-service/api/products/{id}`.

3. **Xử lý Exception & Fallback**:
   - Bắt `HttpClientErrorException.NotFound` (404) -> ném ra `ProductNotFoundException`.
   - Bắt `ResourceAccessException` (Timeout / Mất kết nối) -> trả về `ProductInfo` Fallback an toàn (`status: "FALLBACK"`).

---

## 3. KẾT QUẢ UNIT TEST (JUnit 5 + Mockito)

Tất cả **3 Test Cases** trong `ProductServiceClientRTTest.java` đã chạy **PASS 100%**:

```text
ProductServiceClientRTTest > Test 1: Gọi thành công (Happy Path) - Trả về thông tin sản phẩm chuẩn PASSED
ProductServiceClientRTTest > Test 2: Mô phỏng Timeout (ResourceAccessException) - Trả về dữ liệu Fallback PASSED
ProductServiceClientRTTest > Test 3: Lỗi 404 (HttpClientErrorException.NotFound) - Ném ngoại lệ ProductNotFoundException PASSED

BUILD SUCCESSFUL in 15s
```
