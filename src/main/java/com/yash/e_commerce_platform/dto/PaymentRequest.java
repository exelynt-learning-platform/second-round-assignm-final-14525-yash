package com.yash.e_commerce_platform.dto;

import jakarta.validation.constraints.*;
        import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;   // use "pm_card_visa" for Stripe test
}