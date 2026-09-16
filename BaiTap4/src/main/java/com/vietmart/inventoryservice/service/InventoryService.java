package com.vietmart.inventoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public void deductStock(Long productId, Integer quantity) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Dữ liệu productId không hợp lệ: " + productId);
        }
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Dữ liệu số lượng quantity không hợp lệ: " + quantity);
        }

        log.info("Thực hiện trừ kho thành công cho sản phẩm ID: {}, Số lượng: {}", productId, quantity);
    }
}
