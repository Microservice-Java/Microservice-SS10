# SESSION 10 - MICROSERVICES COMMUNICATIONS, RESILIENCE & EVENT-DRIVEN ERROR HANDLING

Hệ thống **VietMart E-Commerce** - Session 10: Complete Microservices Inter-Service Communications & Event-Driven Fault Tolerance.

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
│       └── test/java/com/vietmart/orderservice/
├── BaiTap2/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap2.md
│   └── src/
│       ├── main/java/com/vietmart/orderservice/
│       └── test/java/com/vietmart/orderservice/
├── BaiTap3/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap3.md
│   └── src/
│       ├── main/java/com/vietmart/promotionservice/
│       └── test/java/com/vietmart/promotionservice/
├── BaiTap4/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap4.md
│   └── src/
│       ├── main/java/com/vietmart/inventoryservice/
│       │   ├── InventoryServiceApplication.java
│       │   ├── config/KafkaConfig.java
│       │   ├── consumer/InventoryConsumer.java
│       │   ├── consumer/InventoryDlqConsumer.java
│       │   ├── model/OrderEvent.java
│       │   └── service/InventoryService.java
│       └── test/java/com/vietmart/inventoryservice/
│           └── consumer/InventoryConsumerTest.java
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

### **Bài tập 2**: Chuyển đổi RestTemplate sang OpenFeign (FeignClient)
- Tạo Declarative Interface `@FeignClient(name = "product-service", fallbackFactory = ProductClientFallbackFactory.class) ProductClient`.
- Tạo `@FeignClient(name = "user-service") UserClient`.
- Triển khai `ProductClientFallbackFactory` log ngoại lệ và trả về dự phòng an toàn.

### **Bài tập 3**: Chuyển đổi RestTemplate sang WebClient trong Spring WebFlux
- Phân tích sự cố cạn kiệt Netty Event Loop Threads khi dùng RestTemplate trong WebFlux dưới tải 10.000 users.
- Chuyển sang `WebClient` với `@LoadBalanced WebClient.Builder` và kiểu trả về Reactive `Mono<Banner>`.
- Cấu hình `.timeout(Duration.ofSeconds(2))` và `.onErrorResume(...)` trả về Fallback Banner mặc định.

### **Bài tập 4**: Xử lý lỗi cho Kafka Consumer với Retry và Dead Letter Queue (DLQ)
- Phân tích cơ chế Kafka Offset commit và lý do Consumer bị kẹt (Poison Pill message) khi gặp Exception.
- Cấu hình `DefaultErrorHandler` thử lại (Retry) tối đa 3 lần (`FixedBackOff(1000L, 2L)`).
- Tích hợp `DeadLetterPublishingRecoverer` đẩy tin nhắn lỗi vào Dead Letter Topic (`order-events.DLT`).
- Tạo `InventoryDlqConsumer` tiếp nhận và giám sát tin nhắn trong DLQ.

---

## 🧪 Kết quả Execution Unit Test

```text
[BaiTap1] ProductServiceClientRTTest -> 3/3 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap2] ProductClientTest          -> 4/4 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap3] PromotionServiceTest       -> 2/2 Tests PASSED (BUILD SUCCESSFUL)
[BaiTap4] InventoryConsumerTest      -> 2/2 Tests PASSED (BUILD SUCCESSFUL)
```
