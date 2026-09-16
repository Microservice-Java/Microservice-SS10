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
│       │   ├── model/ProductInfo.java
│       │   └── model/UserInfo.java
│       └── test/java/com/vietmart/orderservice/
│           └── client/ProductClientTest.java
├── BaiTap3/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap3.md
│   └── src/
│       ├── main/java/com/vietmart/promotionservice/
│       │   ├── PromotionServiceApplication.java
│       │   ├── config/AppConfig.java
│       │   ├── controller/PromotionController.java
│       │   ├── model/Banner.java
│       │   └── service/PromotionService.java
│       └── test/java/com/vietmart/promotionservice/
│           └── service/PromotionServiceTest.java
└── postman/
    ├── SS10_BaiTap1_Collection.json
    ├── SS10_BaiTap2_Collection.json
    └── SS10_BaiTap3_Collection.json
```

---

## 📝 Tóm tắt các Bài tập

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

### **Bài tập 3**: Chuyển đổi RestTemplate sang WebClient trong Spring WebFlux
- Phân tích sự cố cạn kiệt Netty Event Loop Threads khi dùng RestTemplate trong WebFlux dưới tải 10.000 users.
- Chuyển sang `WebClient` với `@LoadBalanced WebClient.Builder` và kiểu trả về Reactive `Mono<Banner>`.
- Cấu hình `.timeout(Duration.ofSeconds(2))` và `.onErrorResume(...)` trả về Fallback Banner mặc định.
- Viết Reactive Unit Test sử dụng `StepVerifier`.

---

## 🧪 Kết quả Execution Unit Test

```text
[BaiTap1] ProductServiceClientRTTest -> 3/3 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap2] ProductClientTest          -> 4/4 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap3] PromotionServiceTest       -> 2/2 Tests PASSED (BUILD SUCCESSFUL)
```
