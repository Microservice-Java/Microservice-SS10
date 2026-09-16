package com.vietmart.orderservice.client;

import com.vietmart.orderservice.exception.ProductNotFoundException;
import com.vietmart.orderservice.model.ProductInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceClientRTTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ProductServiceClientRT productServiceClientRT;

    private ProductInfo mockProduct;

    @BeforeEach
    void setUp() {
        mockProduct = ProductInfo.builder()
                .id(1L)
                .name("Laptop Dell XPS 15")
                .price(1500.0)
                .status("AVAILABLE")
                .build();
    }

    @Test
    @DisplayName("Test 1: Gọi thành công (Happy Path) - Trả về thông tin sản phẩm chuẩn")
    void getById_Success() {
        // Arrange
        when(restTemplate.getForObject(eq("http://product-service/api/products/{id}"), eq(ProductInfo.class), eq(1L)))
                .thenReturn(mockProduct);

        // Act
        ProductInfo result = productServiceClientRT.getById(1L);

        // Assert
        assertNotNull(result, "Kết quả trả về không được null");
        assertEquals(1L, result.getId());
        assertEquals("Laptop Dell XPS 15", result.getName());
        assertEquals(1500.0, result.getPrice());
        assertEquals("AVAILABLE", result.getStatus());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductInfo.class), eq(1L));
    }

    @Test
    @DisplayName("Test 2: Mô phỏng Timeout (ResourceAccessException) - Trả về dữ liệu Fallback")
    void getById_Timeout_ReturnsFallback() {
        // Arrange
        when(restTemplate.getForObject(eq("http://product-service/api/products/{id}"), eq(ProductInfo.class), eq(1L)))
                .thenThrow(new ResourceAccessException("Read timed out after 3000ms"));

        // Act
        ProductInfo result = productServiceClientRT.getById(1L);

        // Assert
        assertNotNull(result, "Fallback trả về không được null");
        assertEquals(1L, result.getId());
        assertEquals("Sản phẩm tạm thời không khả dụng (Fallback)", result.getName());
        assertEquals(0.0, result.getPrice());
        assertEquals("FALLBACK", result.getStatus());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductInfo.class), eq(1L));
    }

    @Test
    @DisplayName("Test 3: Lỗi 404 (HttpClientErrorException.NotFound) - Ném ngoại lệ ProductNotFoundException")
    void getById_NotFound_ThrowsProductNotFoundException() {
        // Arrange
        HttpClientErrorException notFoundException = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND, "Not Found", null, null, null
        );

        when(restTemplate.getForObject(eq("http://product-service/api/products/{id}"), eq(ProductInfo.class), eq(999L)))
                .thenThrow(notFoundException);

        // Act & Assert
        ProductNotFoundException thrown = assertThrows(
                ProductNotFoundException.class,
                () -> productServiceClientRT.getById(999L),
                "Phải ném ra ngoại lệ ProductNotFoundException khi trả về 404"
        );

        assertTrue(thrown.getMessage().contains("Không tìm thấy sản phẩm với ID: 999"));
        verify(restTemplate, times(1)).getForObject(anyString(), eq(ProductInfo.class), eq(999L));
    }
}
