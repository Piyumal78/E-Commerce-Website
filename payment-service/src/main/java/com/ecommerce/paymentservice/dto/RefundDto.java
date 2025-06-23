package com.ecommerce.paymentservice.dto;

import com.ecommerce.paymentservice.entity.RefundStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundDto {
    private Long id;
    private String refundReference;
    private Long paymentId;
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Refund amount must be greater than 0")
    private BigDecimal amount;
    
    private String reason;
    private RefundStatus status;
    private String gatewayRefundId;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
