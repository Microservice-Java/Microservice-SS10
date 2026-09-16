package com.vietmart.promotionservice.controller;

import com.vietmart.promotionservice.model.Banner;
import com.vietmart.promotionservice.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    @Autowired
    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/banner/active")
    public Mono<Banner> getActiveBanner() {
        return promotionService.getActiveBanner();
    }
}
