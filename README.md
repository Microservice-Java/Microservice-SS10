# SESSION 10 - MICROSERVICES COMMUNICATIONS & RESILIENCE

Hệ thống **VietMart E-Commerce** - Session 10: Refactoring & Optimizing Inter-Service Communications.

---

## 📁 Cấu trúc Thư mục Dự án

```text
SS10/
├── BaiTap1/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap1.md
│   └── src/
│       ├── main/java/com/vietmart/orderservice/
│       │   ├── OrderServiceApplication.java
│       │   ├── client/ProductServiceClientRT.java
│       │   ├── config/AppConfig.java
│       │   ├── controller/OrderController.java
│       │   ├── exception/ProductNotFoundException.java
│       │   └── model/ProductInfo.java
│       └── test/java/com/vietmart/orderservice/
│           └── client/ProductServiceClientRTTest.java
├── BaiTap2/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap2.md
│   └── src/
│       ├── main/java/com/vietmart/orderservice/
│       │   ├── OrderServiceApplication.java
│       │   ├── client/ProductClient.java
│       │   ├── client/UserClient.java
│       │   ├── client/fallback/ProductClientFallbackFactory.java
│       │   ├── controller/OrderController.java
│       │   └── model/ProductInfo.java
│       │   └── model/UserInfo.java
│       └── test/java/com/vietmart/orderservice/
│           └── client/ProductClientTest.java
└── postman/
    ├── SS10_BaiTap1_Collection.json
    └── SS10_BaiTap2_Collection.json
```

---

## 📝 Tóm tắt 2 Bài tập

### **Bài tập 1**: Sửa lỗi Hardcode URL trong RestTemplate của Order-Service
- Tháo bỏ IP/Port cứng (`192.168.1.45:8082`), thay bằng `service-id` = `"http://product-service/api/products/{id}"`.
- Cấu hình `@LoadBalanced RestTemplate` Bean có `connectTimeout = 2s` và `readTimeout = 3s`.
- Bắt `HttpClientErrorException.NotFound` (404) -> `ProductNotFoundException`.
- Bắt `ResourceAccessException` (Timeout) -> `Fallback ProductInfo`.

### **Bài tập 2**: Chuyển đổi RestTemplate sang OpenFeign (FeignClient)
- Tạo Declarative Interface `@FeignClient(name = "product-service", fallbackFactory = ProductClientFallbackFactory.class) ProductClient`.
- Tạo `@FeignClient(name = "user-service") UserClient`.
- Triển khai `ProductClientFallbackFactory implements FallbackFactory<ProductClient>` log ngoại lệ và trả về dự phòng an toàn.
- So sánh giảm **>60% số dòng code** và phân tích ưu/nhược điểm trong `BaoCao_BaiTap2.md`.

---

## 🧪 Kết quả Execution Unit Test

```text
[BaiTap1] ProductServiceClientRTTest -> 3/3 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap2] ProductClientTest -> 4/4 Tests PASSED (BUILD SUCCESSFUL)
```
