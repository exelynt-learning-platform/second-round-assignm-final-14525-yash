package com.yash.e_commerce_platform.service;

import com.yash.e_commerce_platform.dto.CartItemRequest;
import com.yash.e_commerce_platform.exception.InsufficientStockException;
import com.yash.e_commerce_platform.exception.ResourceNotFoundException;
import com.yash.e_commerce_platform.model.*;
        import com.yash.e_commerce_platform.repository.*;
        import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public Cart getCart(String email) {
        return cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    public Cart addItem(String email, CartItemRequest req) {
        Cart cart = getCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() < req.getQuantity()) {
            throw new InsufficientStockException(
                    "Insufficient stock for: " + product.getName());
        }

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(req.getProductId()))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + req.getQuantity());
        } else {
            cart.getItems().add(CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(req.getQuantity())
                    .build());
        }
        return cartRepository.save(cart);
    }

    public Cart updateItem(String email, Long itemId, Integer quantity) {
        Cart cart = getCart(email);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (quantity <= 0) {
            cart.getItems().remove(item);
        } else {
            if (item.getProduct().getStockQuantity() < quantity) {
                throw new InsufficientStockException("Insufficient stock");
            }
            item.setQuantity(quantity);
        }
        return cartRepository.save(cart);
    }

    public Cart removeItem(String email, Long itemId) {
        Cart cart = getCart(email);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        return cartRepository.save(cart);
    }

    public void clearCart(Cart cart) {
        cart.getItems().clear();
        cartRepository.save(cart);
    }
}