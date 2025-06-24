package com.ecommerce.paymentservice.dto;

import com.ecommerce.paymentservice.model.PaymentMethod;
import com.ecommerce.paymentservice.model.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {
    
    private Long id;
    private String paymentId;
    private LocalDateTime paymentDate;
    private Double amount;
    private PaymentMethod method;
    private PaymentStatus status;
    
    // Constructors
    public PaymentResponse() {}
    
    public PaymentResponse(Long id, String paymentId, LocalDateTime paymentDate, 
                          Double amount, PaymentMethod method, PaymentStatus status) {
        this.id = id;
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.status = status;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPaymentId() {
        return paymentId;
    }
    
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public PaymentMethod getMethod() {
        return method;
    }
    
    public void setMethod(PaymentMethod method) {
        this.method = method;
    }
    
    public PaymentStatus getStatus() {
        return status;
    }
    
    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
