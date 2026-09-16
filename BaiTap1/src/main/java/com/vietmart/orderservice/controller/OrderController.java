package com.vietmart.orderservice.controller;

import com.vietmart.orderservice.client.ProductServiceClientRT;
import com.vietmart.orderservice.model.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductServiceClientRT productServiceClientRT;

    @Autowired
    public OrderController(ProductServiceClientRT productServiceClientRT) {
        this.productServiceClientRT = productServiceClientRT;
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductInfo> getProductInfo(@PathVariable Long productId) {
        ProductInfo productInfo = productServiceClientRT.getById(productId);
        return ResponseEntity.ok(productInfo);
    }
}
