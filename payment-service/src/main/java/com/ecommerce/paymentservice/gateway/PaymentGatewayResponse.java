package com.ecommerce.paymentservice.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayResponse {
    private boolean success;
    private String transactionId;
    private String message;
    private String errorCode;
    private String gatewayResponse;
}
