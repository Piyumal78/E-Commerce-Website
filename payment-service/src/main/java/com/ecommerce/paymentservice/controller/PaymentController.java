package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentRequest;
import com.ecommerce.paymentservice.dto.PaymentResponse;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse payment = paymentService.createPayment(paymentRequest);
            return new ResponseEntity<>(payment, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        Optional<PaymentResponse> payment = paymentService.getPaymentById(id);
        return payment.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                     .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/payment-id/{paymentId}")
    public ResponseEntity<PaymentResponse> getPaymentByPaymentId(@PathVariable String paymentId) {
        Optional<PaymentResponse> payment = paymentService.getPaymentByPaymentId(paymentId);
        return payment.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                     .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(@PathVariable Long id, 
                                                             @RequestParam PaymentStatus status) {
        try {
            PaymentResponse payment = paymentService.updatePaymentStatus(id, status);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PutMapping("/payment-id/{paymentId}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatusByPaymentId(@PathVariable String paymentId, 
                                                                        @RequestParam PaymentStatus status) {
        try {
            PaymentResponse payment = paymentService.updatePaymentStatusByPaymentId(paymentId, status);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable String paymentId) {
        try {
            PaymentResponse payment = paymentService.processPayment(paymentId);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
