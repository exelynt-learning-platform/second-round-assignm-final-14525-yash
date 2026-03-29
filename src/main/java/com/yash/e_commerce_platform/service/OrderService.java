package com.yash.e_commerce_platform.service;

import com.yash.e_commerce_platform.dto.OrderRequest;
import com.yash.e_commerce_platform.exception.EmptyCartException;
import com.yash.e_commerce_platform.exception.InsufficientStockException;
import com.yash.e_commerce_platform.exception.ResourceNotFoundException;
import com.yash.e_commerce_platform.exception.UnauthorizedAccessException;
import com.yash.e_commerce_platform.model.*;
import com.yash.e_commerce_platform.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    @Transactional
    public Order checkout(String email, OrderRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Cart cart = cartService.getCart(email);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem ci : cart.getItems()) {
            Product p = ci.getProduct();
            if (p.getStockQuantity() < ci.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for: " + p.getName());
            }
            p.setStockQuantity(p.getStockQuantity() - ci.getQuantity());
            productRepository.save(p);

            orderItems.add(OrderItem.builder()
                    .product(p)
                    .quantity(ci.getQuantity())
                    .price(p.getPrice())
                    .build());
            total += p.getPrice() * ci.getQuantity();
        }

        Order order = Order.builder()
                .user(user)
                .totalPrice(total)
                .shippingAddress(req.getShippingAddress())
                .paymentStatus(PaymentStatus.PENDING)
                .items(orderItems)
                .build();

        order = orderRepository.save(order);

        // set back-reference so JPA persists order_items correctly
        for (OrderItem oi : order.getItems()) {
            oi.setOrder(order);
        }
        order = orderRepository.save(order);

        cartService.clearCart(cart);
        return order;
    }

    public List<Order> getUserOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public Order getById(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Access denied to this order");
        }
        return order;
    }
}