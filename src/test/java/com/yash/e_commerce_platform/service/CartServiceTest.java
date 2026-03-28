package com.yash.e_commerce_platform.service;

import com.yash.e_commerce_platform.dto.CartItemRequest;
import com.yash.e_commerce_platform.exception.InsufficientStockException;
import com.yash.e_commerce_platform.model.*;
import com.yash.e_commerce_platform.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
        import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock ProductRepository productRepository;
    @InjectMocks CartService cartService;

    private Cart emptyCart() {
        return Cart.builder().id(1L)
                .user(User.builder().id(1L).email("u@test.com").build())
                .items(new ArrayList<>()).build();
    }

    private Product laptop() {
        return Product.builder().id(1L).name("Laptop")
                .price(999.0).stockQuantity(10).build();
    }

    @Test
    void addItem_success() {
        Cart cart = emptyCart();
        when(cartRepository.findByUserEmail("u@test.com")).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop()));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartItemRequest req = new CartItemRequest();
        req.setProductId(1L); req.setQuantity(2);

        Cart result = cartService.addItem("u@test.com", req);
        assertEquals(1, result.getItems().size());
        assertEquals(2, result.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_insufficientStock() {
        Cart cart = emptyCart();
        when(cartRepository.findByUserEmail("u@test.com")).thenReturn(Optional.of(cart));
        Product p = laptop();
        p.setStockQuantity(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));

        CartItemRequest req = new CartItemRequest();
        req.setProductId(1L); req.setQuantity(5);

        assertThrows(InsufficientStockException.class,
                () -> cartService.addItem("u@test.com", req));
    }

    @Test
    void removeItem() {
        Cart cart = emptyCart();
        CartItem item = CartItem.builder().id(10L).cart(cart)
                .product(laptop()).quantity(1).build();
        cart.getItems().add(item);

        when(cartRepository.findByUserEmail("u@test.com")).thenReturn(Optional.of(cart));
        when(cartRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Cart result = cartService.removeItem("u@test.com", 10L);
        assertTrue(result.getItems().isEmpty());
    }
}