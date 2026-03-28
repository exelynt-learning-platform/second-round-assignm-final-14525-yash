package com.yash.e_commerce_platform.service;


import com.yash.e_commerce_platform.dto.ProductRequest;
import com.yash.e_commerce_platform.exception.ResourceNotFoundException;
import com.yash.e_commerce_platform.model.Product;
import com.yash.e_commerce_platform.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
        import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepository productRepository;
    @InjectMocks ProductService productService;

    private Product sampleProduct() {
        return Product.builder()
                .id(1L).name("Laptop").description("Desc")
                .price(999.99).stockQuantity(10).build();
    }

    @Test
    void getAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct()));
        assertEquals(1, productService.getAll().size());
    }

    @Test
    void getById_found() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct()));
        assertEquals("Laptop", productService.getById(1L).getName());
    }

    @Test
    void getById_notFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));
    }

    @Test
    void createProduct() {
        ProductRequest req = new ProductRequest();
        req.setName("Phone"); req.setPrice(499.0); req.setStockQuantity(5);

        when(productRepository.save(any())).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(2L);
            return p;
        });

        Product result = productService.create(req);
        assertEquals("Phone", result.getName());
        verify(productRepository).save(any());
    }

    @Test
    void deleteProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct()));
        doNothing().when(productRepository).delete(any());
        assertDoesNotThrow(() -> productService.delete(1L));
    }
}