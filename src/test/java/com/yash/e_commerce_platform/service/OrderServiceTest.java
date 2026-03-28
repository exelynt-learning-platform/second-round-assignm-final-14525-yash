package com.yash.e_commerce_platform.service;

import com.yash.e_commerce_platform.dto.OrderRequest;
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
class OrderServiceTest {

    @Mock OrderRepository orderRepository;
    @Mock UserRepository userRepository;
    @Mock ProductRepository productRepository;
    @Mock CartService cartService;
    @InjectMocks OrderService orderService;

    @Test
    void checkout_success() {
        User user = User.builder().id(1L).email("u@test.com").build();
        Product p = Product.builder().id(1L).name("Laptop")
                .price(100.0).stockQuantity(10).build();
        Cart cart = Cart.builder().id(1L).user(user)
                .items(new ArrayList<>()).build();
        cart.getItems().add(CartItem.builder()
                .id(1L).cart(cart).product(p).quantity(2).build());

        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(cartService.getCart("u@test.com")).thenReturn(cart);
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        doNothing().when(cartService).clearCart(any());

        OrderRequest req = new OrderRequest();
        req.setShippingAddress("123 Main St");

        Order order = orderService.checkout("u@test.com", req);

        assertNotNull(order);
        assertEquals(200.0, order.getTotalPrice());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertEquals(8, p.getStockQuantity());   // 10 - 2
    }

    @Test
    void checkout_emptyCart_throws() {
        User user = User.builder().id(1L).email("u@test.com").build();
        Cart cart = Cart.builder().id(1L).user(user)
                .items(new ArrayList<>()).build();

        when(userRepository.findByEmail("u@test.com")).thenReturn(Optional.of(user));
        when(cartService.getCart("u@test.com")).thenReturn(cart);

        OrderRequest req = new OrderRequest();
        req.setShippingAddress("123 Main St");

        assertThrows(RuntimeException.class,
                () -> orderService.checkout("u@test.com", req));
    }
}