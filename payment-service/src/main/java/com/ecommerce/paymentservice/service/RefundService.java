package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.RefundDto;
import com.ecommerce.paymentservice.dto.RefundRequestDto;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.entity.Refund;
import com.ecommerce.paymentservice.entity.RefundStatus;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.exception.RefundException;
import com.ecommerce.paymentservice.exception.RefundNotFoundException;
import com.ecommerce.paymentservice.gateway.PaymentGateway;
import com.ecommerce.paymentservice.gateway.PaymentGatewayResponse;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import com.ecommerce.paymentservice.repository.RefundRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RefundService {
    
    private final RefundRepository refundRepository;
    private final PaymentRepository paymentRepository;
    private final List<PaymentGateway> paymentGateways;
    
    public RefundDto initiateRefund(RefundRequestDto refundRequest) {
        log.info("Initiating refund for payment: {} amount: {}", 
                refundRequest.getPaymentId(), refundRequest.getAmount());
        
        // Validate payment
        Payment payment = paymentRepository.findById(refundRequest.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + refundRequest.getPaymentId()));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RefundException("Cannot refund payment that is not completed");
        }
        
        // Validate refund amount
        validateRefundAmount(payment, refundRequest.getAmount());
        
        // Create refund record
        Refund refund = createRefundRecord(payment, refundRequest);
        refund = refundRepository.save(refund);
        
        // Process refund asynchronously
        processRefundAsync(refund.getId());
        
        return mapToDto(refund);
    }
    
    @Async
    public void processRefundAsync(Long refundId) {
        try {
            Refund refund = refundRepository.findById(refundId)
                    .orElseThrow(() -> new RefundNotFoundException("Refund not found with id: " + refundId));
            
            // Update status to processing
            refund.setStatus(RefundStatus.PROCESSING);
            refundRepository.save(refund);
            
            // Find appropriate gateway
            PaymentGateway gateway = findPaymentGateway(refund.getPayment().getPaymentMethod().name());
            
            // Process refund
            PaymentGatewayResponse response = gateway.refundPayment(
                    refund.getPayment().getGatewayTransactionId(), 
                    refund.getAmount().doubleValue()
            );
            
            // Update refund based on response
            updateRefundFromGatewayResponse(refund, response);
            
            // Update payment status if fully refunded
            updatePaymentRefundStatus(refund.getPayment());
            
        } catch (Exception e) {
            log.error("Error processing refund with id: {}", refundId, e);
            handleRefundFailure(refundId, e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public RefundDto getRefundById(Long id) {
        log.info("Fetching refund with id: {}", id);
        
        Refund refund = refundRepository.findById(id)
                .orElseThrow(() -> new RefundNotFoundException("Refund not found with id: " + id));
        
        return mapToDto(refund);
    }
    
    @Transactional(readOnly = true)
    public RefundDto getRefundByReference(String refundReference) {
        log.info("Fetching refund with reference: {}", refundReference);
        
        Refund refund = refundRepository.findByRefundReference(refundReference)
                .orElseThrow(() -> new RefundNotFoundException("Refund not found with reference: " + refundReference));
        
        return mapToDto(refund);
    }
    
    @Transactional(readOnly = true)
    public List<RefundDto> getRefundsByPaymentId(Long paymentId) {
        log.info("Fetching refunds for payment: {}", paymentId);
        
        List<Refund> refunds = refundRepository.findByPaymentId(paymentId);
        return refunds.stream().map(this::mapToDto).toList();
    }
    
    @Transactional(readOnly = true)
    public Page<RefundDto> getRefundsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching refunds for user: {}", userId);
        
        Page<Refund> refunds = refundRepository.findRefundsByUserId(userId, pageable);
        return refunds.map(this::mapToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<RefundDto> getRefundsByStatus(RefundStatus status, Pageable pageable) {
        log.info("Fetching refunds with status: {}", status);
        
        Page<Refund> refunds = refundRepository.findByStatus(status, pageable);
        return refunds.map(this::mapToDto);
    }
    
    private void validateRefundAmount(Payment payment, BigDecimal refundAmount) {
        BigDecimal totalRefunded = refundRepository.getTotalRefundedAmountForPayment(payment.getId());
        if (totalRefunded == null) {
            totalRefunded = BigDecimal.ZERO;
        }
        
        BigDecimal availableForRefund = payment.getAmount().subtract(totalRefunded);
        
        if (refundAmount.compareTo(availableForRefund) > 0) {
            throw new RefundException("Refund amount exceeds available refund amount. Available: " + availableForRefund);
        }
    }
    
    private Refund createRefundRecord(Payment payment, RefundRequestDto refundRequest) {
        Refund refund = new Refund();
        refund.setRefundReference(generateRefundReference());
        refund.setPayment(payment);
        refund.setAmount(refundRequest.getAmount());
        refund.setReason(refundRequest.getReason());
        refund.setStatus(RefundStatus.PENDING);
        
        return refund;
    }
    
    private void updateRefundFromGatewayResponse(Refund refund, PaymentGatewayResponse response) {
        if (response.isSuccess()) {
            refund.setStatus(RefundStatus.COMPLETED);
            refund.setGatewayRefundId(response.getTransactionId());
            refund.setProcessedAt(LocalDateTime.now());
        } else {
            refund.setStatus(RefundStatus.FAILED);
            refund.setProcessedAt(LocalDateTime.now());
        }
        
        refundRepository.save(refund);
        log.info("Refund {} updated with status: {}", refund.getId(), refund.getStatus());
    }
    
    private void updatePaymentRefundStatus(Payment payment) {
        BigDecimal totalRefunded = refundRepository.getTotalRefundedAmountForPayment(payment.getId());
        if (totalRefunded == null) {
            totalRefunded = BigDecimal.ZERO;
        }
        
        if (totalRefunded.compareTo(payment.getAmount()) == 0) {
            payment.setStatus(PaymentStatus.REFUNDED);
        } else if (totalRefunded.compareTo(BigDecimal.ZERO) > 0) {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        }
        
        paymentRepository.save(payment);
    }
    
    private void handleRefundFailure(Long refundId, String errorMessage) {
        try {
            Refund refund = refundRepository.findById(refundId).orElse(null);
            if (refund != null) {
                refund.setStatus(RefundStatus.FAILED);
                refund.setProcessedAt(LocalDateTime.now());
                refundRepository.save(refund);
            }
        } catch (Exception e) {
            log.error("Error updating refund failure status for refund: {}", refundId, e);
        }
    }
    
    private PaymentGateway findPaymentGateway(String paymentMethod) {
        return paymentGateways.stream()
                .filter(gateway -> gateway.supports(paymentMethod))
                .findFirst()
                .orElseThrow(() -> new RefundException("No gateway found for payment method: " + paymentMethod));
    }
    
    private String generateRefundReference() {
        return "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private RefundDto mapToDto(Refund refund) {
        RefundDto dto = new RefundDto();
        dto.setId(refund.getId());
        dto.setRefundReference(refund.getRefundReference());
        dto.setPaymentId(refund.getPayment().getId());
        dto.setAmount(refund.getAmount());
        dto.setReason(refund.getReason());
        dto.setStatus(refund.getStatus());
        dto.setGatewayRefundId(refund.getGatewayRefundId());
        dto.setProcessedAt(refund.getProcessedAt());
        dto.setCreatedAt(refund.getCreatedAt());
        dto.setUpdatedAt(refund.getUpdatedAt());
        
        return dto;
    }
}
