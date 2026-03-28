package com.yash.e_commerce_platform.repository;

import com.yash.e_commerce_platform.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}