package com.yash.e_commerce_platform.repository;

import com.yash.e_commerce_platform.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserEmail(String email);
}