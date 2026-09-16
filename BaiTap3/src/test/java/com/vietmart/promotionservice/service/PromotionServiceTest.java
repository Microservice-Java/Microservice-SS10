package com.vietmart.promotionservice.service;

import com.vietmart.promotionservice.model.Banner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private PromotionService promotionService;
    private Banner mockBanner;

    @BeforeEach
    void setUp() {
        promotionService = new PromotionService(webClient);

        mockBanner = Banner.builder()
                .id(1L)
                .title("Flash Sale Up To 50%")
                .imageUrl("/banners/flash_sale.jpg")
                .targetUrl("/promotions/flash-sale")
                .status("ACTIVE")
                .build();
    }

    @Test
    @DisplayName("Test 1: Gọi WebClient thành công - Trả về Active Banner chuẩn")
    void getActiveBanner_Success() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Banner.class)).thenReturn(Mono.just(mockBanner));

        Mono<Banner> bannerMono = promotionService.getActiveBanner();

        StepVerifier.create(bannerMono)
                .expectNextMatches(banner ->
                        banner.getId().equals(1L) &&
                        banner.getTitle().equals("Flash Sale Up To 50%") &&
                        banner.getStatus().equals("ACTIVE")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Test 2: Mô phỏng Timeout (2s) - Trả về Fallback Banner mặc định")
    void getActiveBanner_Timeout_ReturnsDefaultFallback() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Banner.class)).thenReturn(Mono.error(new TimeoutException("Request timed out after 2000ms")));

        Mono<Banner> bannerMono = promotionService.getActiveBanner();

        StepVerifier.create(bannerMono)
                .expectNextMatches(banner ->
                        banner.getId().equals(0L) &&
                        banner.getTitle().equals("Khuyến mãi đang được cập nhật") &&
                        banner.getStatus().equals("DEFAULT") &&
                        banner.getImageUrl().equals("/banners/default.jpg")
                )
                .verifyComplete();
    }
}
