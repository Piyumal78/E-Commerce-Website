package com.ecommerce.paymentservice.gateway;

import com.ecommerce.paymentservice.dto.PaymentRequestDto;

public interface PaymentGateway {
    PaymentGatewayResponse processPayment(PaymentRequestDto paymentRequest);
    PaymentGatewayResponse refundPayment(String transactionId, Double amount);
    boolean supports(String paymentMethod);
}
