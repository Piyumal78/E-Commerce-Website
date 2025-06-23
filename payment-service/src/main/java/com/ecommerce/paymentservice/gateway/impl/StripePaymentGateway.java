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
public class StripePaymentGateway implements PaymentGateway {
    
    @Override
    public PaymentGatewayResponse processPayment(PaymentRequestDto paymentRequest) {
        log.info("Processing Stripe payment for amount: {}", paymentRequest.getAmount());
        
        try {
            // Simulate Stripe API call
            Thread.sleep(1000); // Simulate network delay
            
            // Mock success/failure logic (90% success rate)
            boolean success = Math.random() > 0.1;
            
            if (success) {
                return PaymentGatewayResponse.builder()
                        .success(true)
                        .transactionId("stripe_" + UUID.randomUUID().toString())
                        .message("Payment processed successfully")
                        .gatewayResponse("Payment completed via Stripe")
                        .build();
            } else {
                return PaymentGatewayResponse.builder()
                        .success(false)
                        .errorCode("CARD_DECLINED")
                        .message("Card was declined")
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
        log.info("Processing Stripe refund for transaction: {} amount: {}", transactionId, amount);
        
        try {
            Thread.sleep(500); // Simulate network delay
            
            return PaymentGatewayResponse.builder()
                    .success(true)
                    .transactionId("refund_" + UUID.randomUUID().toString())
                    .message("Refund processed successfully")
                    .gatewayResponse("Refund completed via Stripe")
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
        return PaymentMethod.STRIPE.name().equals(paymentMethod) || 
               PaymentMethod.CREDIT_CARD.name().equals(paymentMethod) ||
               PaymentMethod.DEBIT_CARD.name().equals(paymentMethod);
    }
}
