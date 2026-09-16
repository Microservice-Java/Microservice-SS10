package com.vietmart.promotionservice.service;

import com.vietmart.promotionservice.model.Banner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class PromotionService {

    private static final Logger log = LoggerFactory.getLogger(PromotionService.class);
    private final WebClient webClient;

    @Autowired
    public PromotionService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<Banner> getActiveBanner() {
        String url = "http://promotion-service/api/banners/active";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Banner.class)
                .timeout(Duration.ofSeconds(2))
                .onErrorResume(throwable -> {
                    log.warn("Xảy ra sự cố khi gọi promotion-service qua WebClient (Timeout/Lỗi). Chi tiết: {}. Trả về Banner mặc định.", throwable.getMessage());
                    return Mono.just(getDefaultBanner());
                });
    }

    public Banner getDefaultBanner() {
        return Banner.builder()
                .id(0L)
                .title("Khuyến mãi đang được cập nhật")
                .imageUrl("/banners/default.jpg")
                .targetUrl("/promotions")
                .status("DEFAULT")
                .build();
    }
}
