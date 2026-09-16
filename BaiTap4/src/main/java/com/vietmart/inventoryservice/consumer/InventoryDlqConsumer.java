package com.vietmart.inventoryservice.consumer;

import com.vietmart.inventoryservice.model.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryDlqConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryDlqConsumer.class);

    @KafkaListener(topics = "order-events.DLT", groupId = "inventory-dlq-group")
    public void consumeDlq(OrderEvent failedEvent) {
        log.error("[DEAD LETTER QUEUE - DLT] Đã nhận tin nhắn bị lỗi sau 3 lần retry: OrderId={}, ProductId={}, Quantity={}",
                failedEvent.getOrderId(), failedEvent.getProductId(), failedEvent.getQuantity());
    }
}
