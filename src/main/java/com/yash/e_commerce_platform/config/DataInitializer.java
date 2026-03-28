package com.yash.e_commerce_platform.config;

import com.yash.e_commerce_platform.model.*;
import com.yash.e_commerce_platform.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Admin user
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = userRepository.save(User.builder()
                    .name("Admin")
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .build());
            cartRepository.save(Cart.builder()
                    .user(admin).items(new ArrayList<>()).build());
        }

        // Sample products
        if (productRepository.count() == 0) {
            productRepository.saveAll(List.of(
                    Product.builder().name("Laptop").description("High-performance laptop")
                            .price(999.99).stockQuantity(50)
                            .imageUrl("https://example.com/laptop.jpg").build(),
                    Product.builder().name("Smartphone").description("Latest smartphone")
                            .price(699.99).stockQuantity(100)
                            .imageUrl("https://example.com/phone.jpg").build(),
                    Product.builder().name("Headphones").description("Wireless headphones")
                            .price(149.99).stockQuantity(200)
                            .imageUrl("https://example.com/headphones.jpg").build()
            ));
        }
    }
}