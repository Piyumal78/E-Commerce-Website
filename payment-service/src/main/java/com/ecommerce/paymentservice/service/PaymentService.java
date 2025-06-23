package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentDto;
import com.ecommerce.paymentservice.dto.PaymentRequestDto;
import com.ecommerce.paymentservice.entity.Payment;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.exception.PaymentNotFoundException;
import com.ecommerce.paymentservice.exception.PaymentProcessingException;
import com.ecommerce.paymentservice.gateway.PaymentGateway;
import com.ecommerce.paymentservice.gateway.PaymentGatewayResponse;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final List<PaymentGateway> paymentGateways;
    
    public PaymentDto initiatePayment(PaymentRequestDto paymentRequest) {
        log.info("Initiating payment for order: {} user: {} amount: {}", 
                paymentRequest.getOrderId(), paymentRequest.getUserId(), paymentRequest.getAmount());
        
        // Create payment record
        Payment payment = createPaymentRecord(paymentRequest);
        payment = paymentRepository.save(payment);
        
        // Process payment asynchronously
        processPaymentAsync(payment.getId(), paymentRequest);
        
        return mapToDto(payment);
    }
    
    @Async
    public void processPaymentAsync(Long paymentId, PaymentRequestDto paymentRequest) {
        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
            
            // Update status to processing
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            
            // Find appropriate gateway
            PaymentGateway gateway = findPaymentGateway(paymentRequest.getPaymentMethod().name());
            
            // Process payment
            PaymentGatewayResponse response = gateway.processPayment(paymentRequest);
            
            // Update payment based on response
            updatePaymentFromGatewayResponse(payment, response);
            
        } catch (Exception e) {
            log.error("Error processing payment with id: {}", paymentId, e);
            handlePaymentFailure(paymentId, e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public PaymentDto getPaymentById(Long id) {
        log.info("Fetching payment with id: {}", id);
        
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
        
        return mapToDto(payment);
    }
    
    @Transactional(readOnly = true)
    public PaymentDto getPaymentByReference(String paymentReference) {
        log.info("Fetching payment with reference: {}", paymentReference);
        
        Payment payment = paymentRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with reference: " + paymentReference));
        
        return mapToDto(payment);
    }
    
    @Transactional(readOnly = true)
    public List<PaymentDto> getPaymentsByOrderId(Long orderId) {
        log.info("Fetching payments for order: {}", orderId);
        
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        return payments.stream().map(this::mapToDto).toList();
    }
    
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPaymentsByUserId(Long userId, Pageable pageable) {
        log.info("Fetching payments for user: {}", userId);
        
        Page<Payment> payments = paymentRepository.findByUserId(userId, pageable);
        return payments.map(this::mapToDto);
    }
    
    @Transactional(readOnly = true)
    public Page<PaymentDto> getPaymentsByStatus(List<PaymentStatus> statuses, Pageable pageable) {
        log.info("Fetching payments with statuses: {}", statuses);
        
        Page<Payment> payments = paymentRepository.findByStatusIn(statuses, pageable);
        return payments.map(this::mapToDto);
    }
    
    public PaymentDto updatePaymentStatus(Long paymentId, PaymentStatus status) {
        log.info("Updating payment {} status to {}", paymentId, status);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + paymentId));
        
        payment.setStatus(status);
        if (status == PaymentStatus.COMPLETED || status == PaymentStatus.FAILED) {
            payment.setProcessedAt(LocalDateTime.now());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToDto(updatedPayment);
    }
    
    private Payment createPaymentRecord(PaymentRequestDto paymentRequest) {
        Payment payment = new Payment();
        payment.setPaymentReference(generatePaymentReference());
        payment.setOrderId(paymentRequest.getOrderId());
        payment.setUserId(paymentRequest.getUserId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        
        return payment;
    }
    
    private void updatePaymentFromGatewayResponse(Payment payment, PaymentGatewayResponse response) {
        if (response.isSuccess()) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setGatewayTransactionId(response.getTransactionId());
            payment.setGatewayResponse(response.getGatewayResponse());
            payment.setProcessedAt(LocalDateTime.now());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(response.getMessage());
            payment.setGatewayResponse(response.getGatewayResponse());
            payment.setProcessedAt(LocalDateTime.now());
        }
        
        paymentRepository.save(payment);
        log.info("Payment {} updated with status: {}", payment.getId(), payment.getStatus());
    }
    
    private void handlePaymentFailure(Long paymentId, String errorMessage) {
        try {
            Payment payment = paymentRepository.findById(paymentId).orElse(null);
            if (payment != null) {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(errorMessage);
                payment.setProcessedAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }
        } catch (Exception e) {
            log.error("Error updating payment failure status for payment: {}", paymentId, e);
        }
    }
    
    private PaymentGateway findPaymentGateway(String paymentMethod) {
        return paymentGateways.stream()
                .filter(gateway -> gateway.supports(paymentMethod))
                .findFirst()
                .orElseThrow(() -> new PaymentProcessingException("No gateway found for payment method: " + paymentMethod));
    }
    
    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
    
    private PaymentDto mapToDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId());
        dto.setPaymentReference(payment.getPaymentReference());
        dto.setOrderId(payment.getOrderId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setGatewayTransactionId(payment.getGatewayTransactionId());
        dto.setGatewayResponse(payment.getGatewayResponse());
        dto.setFailureReason(payment.getFailureReason());
        dto.setProcessedAt(payment.getProcessedAt());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());
        
        return dto;
    }
}
