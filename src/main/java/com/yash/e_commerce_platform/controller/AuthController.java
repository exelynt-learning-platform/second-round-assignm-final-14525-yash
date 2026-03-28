package com.yash.e_commerce_platform.controller;

import com.yash.e_commerce_platform.dto.*;
import com.yash.e_commerce_platform.exception.ResourceNotFoundException;
import com.yash.e_commerce_platform.model.*;
import com.yash.e_commerce_platform.repository.CartRepository;
import com.yash.e_commerce_platform.repository.UserRepository;
import com.yash.e_commerce_platform.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
        import org.springframework.security.authentication.*;
        import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

        import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email already registered"));
        }

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();
        user = userRepository.save(user);

        // create an empty cart for the new user
        cartRepository.save(Cart.builder()
                .user(user)
                .items(new ArrayList<>())
                .build());

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(
                new AuthResponse(token, user.getEmail(), user.getRole().name()));
    }
}