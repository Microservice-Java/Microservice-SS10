package com.vietmart.orderservice.client;

import com.vietmart.orderservice.exception.ProductNotFoundException;
import com.vietmart.orderservice.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceClientRT {

    private final RestTemplate restTemplate;

    @Autowired
    public ProductServiceClientRT(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ProductInfo getById(Long productId) {
        // Sử dụng service-id = "product-service" thay vì IP cứng
        String url = "http://product-service/api/products/{id}";

        try {
            ProductInfo response = restTemplate.getForObject(url, ProductInfo.class, productId);
            if (response == null) {
                return getFallbackProductInfo(productId);
            }
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            // 404 Not Found -> Throw ProductNotFoundException
            throw new ProductNotFoundException("Không tìm thấy sản phẩm với ID: " + productId);
        } catch (ResourceAccessException e) {
            // Timeout / Mất kết nối mạng -> Trả về Fallback ProductInfo
            return getFallbackProductInfo(productId);
        } catch (Exception e) {
            // Mọi sự cố không xác định -> Trả về Fallback an toàn
            return getFallbackProductInfo(productId);
        }
    }

    private ProductInfo getFallbackProductInfo(Long productId) {
        return ProductInfo.builder()
                .id(productId)
                .name("Sản phẩm tạm thời không khả dụng (Fallback)")
                .price(0.0)
                .status("FALLBACK")
                .build();
    }
}
