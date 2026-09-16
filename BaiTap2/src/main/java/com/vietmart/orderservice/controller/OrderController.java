package com.vietmart.orderservice.controller;

import com.vietmart.orderservice.client.ProductClient;
import com.vietmart.orderservice.client.UserClient;
import com.vietmart.orderservice.model.ProductInfo;
import com.vietmart.orderservice.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final ProductClient productClient;
    private final UserClient userClient;

    @Autowired
    public OrderController(ProductClient productClient, UserClient userClient) {
        this.productClient = productClient;
        this.userClient = userClient;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductInfo> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productClient.getById(id));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductInfo>> getAllProducts() {
        return ResponseEntity.ok(productClient.getAll());
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserInfo> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userClient.getUserById(userId));
    }
}
