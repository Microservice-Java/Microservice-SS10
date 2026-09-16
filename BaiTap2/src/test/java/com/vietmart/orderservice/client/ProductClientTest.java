package com.vietmart.orderservice.client;

import com.vietmart.orderservice.client.fallback.ProductClientFallbackFactory;
import com.vietmart.orderservice.model.ProductInfo;
import com.vietmart.orderservice.model.UserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductClientTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private UserClient userClient;

    private ProductClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new ProductClientFallbackFactory();
    }

    @Test
    @DisplayName("Test 1: Feign Client ProductClient.getById thành công")
    void testGetById_Success() {
        ProductInfo mockProduct = ProductInfo.builder()
                .id(1L)
                .name("iPhone 15 Pro Max")
                .price(1200.0)
                .status("AVAILABLE")
                .build();

        when(productClient.getById(1L)).thenReturn(mockProduct);

        ProductInfo result = productClient.getById(1L);

        assertNotNull(result);
        assertEquals("iPhone 15 Pro Max", result.getName());
        assertEquals(1200.0, result.getPrice());
        verify(productClient, times(1)).getById(1L);
    }

    @Test
    @DisplayName("Test 2: Feign FallbackFactory kích hoạt khi có sự cố getById")
    void testFallbackFactory_GetById() {
        Throwable cause = new RuntimeException("Connection timed out to product-service");
        ProductClient fallbackClient = fallbackFactory.create(cause);

        ProductInfo fallbackProduct = fallbackClient.getById(1L);

        assertNotNull(fallbackProduct);
        assertEquals(1L, fallbackProduct.getId());
        assertEquals("Sản phẩm tạm thời không khả dụng (Feign Fallback)", fallbackProduct.getName());
        assertEquals("FALLBACK", fallbackProduct.getStatus());
    }

    @Test
    @DisplayName("Test 3: Feign FallbackFactory kích hoạt cho getAll() - Trả danh sách rỗng")
    void testFallbackFactory_GetAll() {
        Throwable cause = new RuntimeException("Service Unavailable 503");
        ProductClient fallbackClient = fallbackFactory.create(cause);

        List<ProductInfo> fallbackList = fallbackClient.getAll();

        assertNotNull(fallbackList);
        assertTrue(fallbackList.isEmpty());
    }

    @Test
    @DisplayName("Test 4: UserClient.getUserById thành công")
    void testUserClient_GetUserById() {
        UserInfo mockUser = UserInfo.builder()
                .id(10L)
                .username("nguyenvana")
                .email("a@gmail.com")
                .fullName("Nguyen Van A")
                .role("CUSTOMER")
                .build();

        when(userClient.getUserById(10L)).thenReturn(mockUser);

        UserInfo result = userClient.getUserById(10L);

        assertNotNull(result);
        assertEquals("Nguyen Van A", result.getFullName());
        assertEquals("CUSTOMER", result.getRole());
    }
}
