package com.vietmart.inventoryservice.consumer;

import com.vietmart.inventoryservice.model.OrderEvent;
import com.vietmart.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryConsumerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryConsumer inventoryConsumer;

    private OrderEvent validEvent;
    private OrderEvent invalidEvent;

    @BeforeEach
    void setUp() {
        validEvent = OrderEvent.builder()
                .orderId("ORD-1001")
                .productId(101L)
                .quantity(2)
                .build();

        invalidEvent = OrderEvent.builder()
                .orderId("ORD-9999")
                .productId(-1L)
                .quantity(-5)
                .build();
    }

    @Test
    @DisplayName("Test 1: Consumer xử lý tin nhắn thành công (Happy Path)")
    void consume_Success() {
        doNothing().when(inventoryService).deductStock(101L, 2);

        inventoryConsumer.consume(validEvent);

        verify(inventoryService, times(1)).deductStock(101L, 2);
    }

    @Test
    @DisplayName("Test 2: Consumer gặp dữ liệu hỏng văng Exception (Trigger Retry & DLQ)")
    void consume_InvalidData_ThrowsException() {
        doThrow(new IllegalArgumentException("Dữ liệu số lượng quantity không hợp lệ: -5"))
                .when(inventoryService).deductStock(-1L, -5);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> inventoryConsumer.consume(invalidEvent)
        );

        assertTrue(thrown.getMessage().contains("Dữ liệu số lượng quantity không hợp lệ"));
        verify(inventoryService, times(1)).deductStock(-1L, -5);
    }
}
