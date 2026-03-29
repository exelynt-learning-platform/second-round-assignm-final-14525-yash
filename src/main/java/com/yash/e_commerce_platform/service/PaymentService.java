package com.yash.e_commerce_platform.service;

import com.yash.e_commerce_platform.exception.EmptyCartException;
import com.yash.e_commerce_platform.exception.PaymentException;
import com.yash.e_commerce_platform.exception.ResourceNotFoundException;
import com.yash.e_commerce_platform.exception.UnauthorizedAccessException;
import com.yash.e_commerce_platform.model.Order;
import com.yash.e_commerce_platform.model.PaymentStatus;
import com.yash.e_commerce_platform.repository.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;

    public Map<String, String> processPayment(Long orderId,
            String paymentMethodId,
            String email) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getEmail().equals(email)) {
            throw new UnauthorizedAccessException("Access denied");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new EmptyCartException("Order is already paid");
        }

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (order.getTotalPrice() * 100)) // cents
                    .setCurrency("usd")
                    .setDescription("Order #" + order.getId())
                    .setPaymentMethod(paymentMethodId)
                    .setConfirm(true) // charge immediately
                    .addPaymentMethodType("card")
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            if ("succeeded".equals(intent.getStatus())) {
                order.setPaymentStatus(PaymentStatus.PAID);
                order.setStripePaymentId(intent.getId());
                orderRepository.save(order);

                Map<String, String> resp = new HashMap<>();
                resp.put("message", "Payment successful");
                resp.put("paymentIntentId", intent.getId());
                resp.put("status", "PAID");
                return resp;
            } else {
                order.setPaymentStatus(PaymentStatus.FAILED);
                orderRepository.save(order);
                throw new PaymentException("Payment incomplete – status: " + intent.getStatus());
            }
        } catch (StripeException e) {
            order.setPaymentStatus(PaymentStatus.FAILED);
            orderRepository.save(order);
            throw new PaymentException("Stripe error: " + e.getMessage());
        }
    }
}