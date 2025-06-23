package com.ecommerce.paymentservice.gateway.impl;

import com.ecommerce.paymentservice.dto.PaymentRequestDto;
import com.ecommerce.paymentservice.entity.PaymentMethod;
import com.ecommerce.paymentservice.gateway.PaymentGateway;
import com.ecommerce.paymentservice.gateway.PaymentGatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class PayPalPaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentGatewayResponse processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing PayPal payment for amount: {}", paymentRequest.getAmount());
        
        try {
            // Simulate PayPal API call
            Thread.sleep(1500); // Simulate network delay
            
            // Mock success/failure logic (95% success rate)
            boolean success = Math.random() > 0.05;
            
            if (success) {
                return PaymentGatewayResponse.builder()
                        .success(true)
                        .transactionId("paypal_" + UUID.randomUUID().toString())
                        .message("Payment processed successfully")
                        .gatewayResponse("Payment completed via PayPal")
                        .build();
            } else {
                return PaymentGatewayResponse.builder()
                        .success(false)
                        .errorCode("INSUFFICIENT_FUNDS")
                        .message("Insufficient funds in PayPal account")
                        .gatewayResponse("Payment failed - insufficient funds")
                        .build();
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentGatewayResponse.builder()
                    .success(false)
                    .errorCode("GATEWAY_ERROR")
                    .message("Gateway communication error")
                    .build();
        }
    }
    
    @Override
    public PaymentGatewayResponse refundPayment(String transactionId, Double amount) {
        log.info("Processing PayPal refund for transaction: {} amount: {}", transactionId, amount);
        
        try {
            Thread.sleep(800); // Simulate network delay
            
            return PaymentGatewayResponse.builder()
                    .success(true)
                    .transactionId("paypal_refund_" + UUID.randomUUID().toString())
                    .message("Refund processed successfully")
                    .gatewayResponse("Refund completed via PayPal")
                    .build();
                    
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentGatewayResponse.builder()
                    .success(false)
                    .errorCode("REFUND_ERROR")
                    .message("Refund processing failed")
                    .build();
        }
    }
    
    @Override
    public boolean supports(String paymentMethod) {
        return PaymentMethod.PAYPAL.name().equals(paymentMethod);
    }
}
