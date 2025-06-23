package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.PaymentDto;
import com.ecommerce.paymentservice.dto.PaymentRequestDto;
import com.ecommerce.paymentservice.entity.PaymentStatus;
import com.ecommerce.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {
    
    private final PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<PaymentDto> initiatePayment(@Valid @RequestBody PaymentRequestDto paymentRequest) {
        PaymentDto payment = paymentService.initiatePayment(paymentRequest);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Long id) {
        PaymentDto payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/reference/{paymentReference}")
    public ResponseEntity<PaymentDto> getPaymentByReference(@PathVariable String paymentReference) {
        PaymentDto payment = paymentService.getPaymentByReference(paymentReference);
        return ResponseEntity.ok(payment);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByOrderId(@PathVariable Long orderId) {
        List<PaymentDto> payments = paymentService.getPaymentsByOrderId(orderId);
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PaymentDto> payments = paymentService.getPaymentsByUserId(userId, pageable);
        
        return ResponseEntity.ok(payments);
    }
    
    @GetMapping
    public ResponseEntity<Page<PaymentDto>> getPaymentsByStatus(
            @RequestParam List<PaymentStatus> statuses,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PaymentDto> payments = paymentService.getPaymentsByStatus(statuses, pageable);
        
        return ResponseEntity.ok(payments);
    }
    
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentDto> updatePaymentStatus(@PathVariable Long id, 
                                                         @RequestParam PaymentStatus status) {
        PaymentDto payment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(payment);
    }
}
