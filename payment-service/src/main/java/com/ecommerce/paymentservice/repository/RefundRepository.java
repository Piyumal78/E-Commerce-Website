package com.ecommerce.paymentservice.repository;

import com.ecommerce.paymentservice.entity.Refund;
import com.ecommerce.paymentservice.entity.RefundStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRepository extends JpaRepository<Refund, Long> {
    
    Optional<Refund> findByRefundReference(String refundReference);
    
    List<Refund> findByPaymentId(Long paymentId);
    
    Page<Refund> findByStatus(RefundStatus status, Pageable pageable);
    
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.payment.id = :paymentId AND r.status = 'COMPLETED'")
    BigDecimal getTotalRefundedAmountForPayment(@Param("paymentId") Long paymentId);
    
    @Query("SELECT r FROM Refund r WHERE r.payment.userId = :userId")
    Page<Refund> findRefundsByUserId(@Param("userId") Long userId, Pageable pageable);
}
