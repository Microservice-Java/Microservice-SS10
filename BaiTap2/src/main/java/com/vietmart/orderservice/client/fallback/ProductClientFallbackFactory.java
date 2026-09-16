package com.vietmart.orderservice.client.fallback;

import com.vietmart.orderservice.client.ProductClient;
import com.vietmart.orderservice.model.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    private static final Logger log = LoggerFactory.getLogger(ProductClientFallbackFactory.class);

    @Override
    public ProductClient create(Throwable cause) {
        log.error("Lỗi khi gọi product-service qua OpenFeign Client. Chi tiết ngoại lệ: {}", cause.getMessage(), cause);

        return new ProductClient() {
            @Override
            public ProductInfo getById(Long id) {
                log.warn("Kích hoạt Feign Fallback cho getById() với productId: {}", id);
                return ProductInfo.builder()
                        .id(id)
                        .name("Sản phẩm tạm thời không khả dụng (Feign Fallback)")
                        .price(0.0)
                        .status("FALLBACK")
                        .build();
            }

            @Override
            public List<ProductInfo> getAll() {
                log.warn("Kích hoạt Feign Fallback cho getAll() - Trả về danh sách rỗng");
                return Collections.emptyList();
            }
        };
    }
}
