package com.yash.e_commerce_platform.repository;

import com.yash.e_commerce_platform.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}