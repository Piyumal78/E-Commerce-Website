package com.ecommerce.paymentservice.service;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public PaymentResponse createPayment(PaymentRequest paymentRequest) {
        // Generate unique payment ID if not provided
        String paymentId = paymentRequest.getPaymentId();
        if (paymentId == null || paymentId.trim().isEmpty()) {
            paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        
        // Check if payment ID already exists
        if (paymentRepository.existsByPaymentId(paymentId)) {
            throw new RuntimeException("Payment ID already exists: " + paymentId);
        }
        
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        payment.setPaymentDate(paymentRequest.getPaymentDate() != null ? 
                              paymentRequest.getPaymentDate() : LocalDateTime.now());
        payment.setAmount(paymentRequest.getAmount());
        payment.setMethod(paymentRequest.getMethod());
        payment.setStatus(paymentRequest.getStatus() != null ? 
                         paymentRequest.getStatus() : PaymentStatus.PENDING);
        
        Payment savedPayment = paymentRepository.save(payment);
        return convertToResponse(savedPayment);
    }
    
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public Optional<PaymentResponse> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    public Optional<PaymentResponse> getPaymentByPaymentId(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .map(this::convertToResponse);
    }
    
    public List<PaymentResponse> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public PaymentResponse updatePaymentStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }
    
    public PaymentResponse updatePaymentStatusByPaymentId(String paymentId, PaymentStatus status) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
        
        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);
        return convertToResponse(updatedPayment);
    }
    
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
    
    public PaymentResponse processPayment(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with paymentId: " + paymentId));
        
        // Simulate payment processing logic
        try {
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            
            // Simulate processing time
            Thread.sleep(1000);
            
            // Simulate successful payment (90% success rate)
            if (Math.random() > 0.1) {
                payment.setStatus(PaymentStatus.COMPLETED);
            } else {
                payment.setStatus(PaymentStatus.FAILED);
            }
            
            Payment processedPayment = paymentRepository.save(payment);
            return convertToResponse(processedPayment);
            
        } catch (InterruptedException e) {
            payment.setStatus(PaymentStatus.FAILED);
            Payment failedPayment = paymentRepository.save(payment);
            return convertToResponse(failedPayment);
        }
    }
    
    private PaymentResponse convertToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getPaymentId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus()
        );
    }
}
