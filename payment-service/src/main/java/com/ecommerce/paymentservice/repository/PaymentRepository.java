package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.model.Payment;
import com.ecommerce.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentId(String paymentId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByAmountBetween(Double minAmount, Double maxAmount);
    
    boolean existsByPaymentId(String paymentId);
}
