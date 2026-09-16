# SESSION 10 - BÀI TẬP 1: SỬA LỖI HARDCODE URL TRONG RESTTEMPLATE CỦA ORDER-SERVICE

Hệ thống **VietMart E-Commerce** - Refactoring & Fixing `ProductServiceClientRT` trong `order-service`.

---

## 📁 Cấu trúc Thư mục Dự án

```text
SS10/
├── BaiTap1/
│   ├── build.gradle
│   ├── settings.gradle
│   ├── BaoCao_BaiTap1.md
│   ├── src/
│   │   ├── main/java/com/vietmart/orderservice/
│   │   │   ├── OrderServiceApplication.java
│   │   │   ├── client/ProductServiceClientRT.java
│   │   │   ├── config/AppConfig.java
│   │   │   ├── controller/OrderController.java
│   │   │   ├── exception/ProductNotFoundException.java
│   │   │   └── model/ProductInfo.java
│   │   └── resources/application.yml
│   └── src/test/java/com/vietmart/orderservice/
│       └── client/ProductServiceClientRTTest.java
└── postman/
    └── SS10_BaiTap1_Collection.json
```

---

## 🛠 Chi tiết Sửa lỗi trong `ProductServiceClientRT`

### 1. `AppConfig.java`:
```java
@Configuration
public class AppConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(3))
                .build();
    }
}
```

### 2. `ProductServiceClientRT.java`:
```java
@Component
public class ProductServiceClientRT {
    private final RestTemplate restTemplate;

    @Autowired
    public ProductServiceClientRT(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductInfo getById(Long productId) {
        String url = "http://product-service/api/products/{id}";

        try {
            ProductInfo response = restTemplate.getForObject(url, ProductInfo.class, productId);
            return response != null ? response : getFallbackProductInfo(productId);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        } catch (ResourceAccessException e) {
            return getFallbackProductInfo(productId);
        } catch (Exception e) {
            return getFallbackProductInfo(productId);
        }
    }
}
```

---

## 🧪 Kết quả Chạy Unit Test (`gradlew test`)

```text
ProductServiceClientRTTest > Test 1: Happy Path PASSED
ProductServiceClientRTTest > Test 2: Timeout & Fallback PASSED
ProductServiceClientRTTest > Test 3: 404 Exception PASSED

BUILD SUCCESSFUL
```
