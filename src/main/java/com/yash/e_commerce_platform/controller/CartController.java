package com.yash.e_commerce_platform.controller;

import com.yash.e_commerce_platform.dto.CartItemRequest;
import com.yash.e_commerce_platform.model.Cart;
import com.yash.e_commerce_platform.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        return ResponseEntity.ok(cartService.getCart(principal.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItem(@Valid @RequestBody CartItemRequest req,
                                        Principal principal) {
        return ResponseEntity.ok(cartService.addItem(principal.getName(), req));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateItem(@PathVariable Long itemId,
                                           @RequestParam Integer quantity,
                                           Principal principal) {
        return ResponseEntity.ok(
                cartService.updateItem(principal.getName(), itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(@PathVariable Long itemId,
                                           Principal principal) {
        return ResponseEntity.ok(cartService.removeItem(principal.getName(), itemId));
    }
}