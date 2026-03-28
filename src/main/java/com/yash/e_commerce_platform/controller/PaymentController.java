package com.yash.e_commerce_platform.controller;

import com.yash.e_commerce_platform.dto.PaymentRequest;
import com.yash.e_commerce_platform.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<Map<String, String>> pay(
            @Valid @RequestBody PaymentRequest req, Principal principal) {
        return ResponseEntity.ok(
                paymentService.processPayment(
                        req.getOrderId(), req.getPaymentMethodId(), principal.getName()));
    }
}