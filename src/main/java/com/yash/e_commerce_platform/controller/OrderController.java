package com.yash.e_commerce_platform.controller;

import com.yash.e_commerce_platform.dto.OrderRequest;
import com.yash.e_commerce_platform.model.Order;
import com.yash.e_commerce_platform.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.web.bind.annotation.*;

        import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(@Valid @RequestBody OrderRequest req,
                                          Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.checkout(principal.getName(), req));
    }

    @GetMapping
    public ResponseEntity<List<Order>> myOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.getName()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id, Principal principal) {
        return ResponseEntity.ok(orderService.getById(id, principal.getName()));
    }
}