package com.ecommerce.paymentservice.controller;

import com.ecommerce.paymentservice.dto.RefundDto;
import com.ecommerce.paymentservice.dto.RefundRequestDto;
import com.ecommerce.paymentservice.entity.RefundStatus;
import com.ecommerce.paymentservice.service.RefundService;
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
@RequestMapping("/api/refunds")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RefundController {
    
    private final RefundService refundService;
    
    @PostMapping
    public ResponseEntity<RefundDto> initiateRefund(@Valid @RequestBody RefundRequestDto refundRequest) {
        RefundDto refund = refundService.initiateRefund(refundRequest);
        return new ResponseEntity<>(refund, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RefundDto> getRefundById(@PathVariable Long id) {
        RefundDto refund = refundService.getRefundById(id);
        return ResponseEntity.ok(refund);
    }
    
    @GetMapping("/reference/{refundReference}")
    public ResponseEntity<RefundDto> getRefundByReference(@PathVariable String refundReference) {
        RefundDto refund = refundService.getRefundByReference(refundReference);
        return ResponseEntity.ok(refund);
    }
    
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<RefundDto>> getRefundsByPaymentId(@PathVariable Long paymentId) {
        List<RefundDto> refunds = refundService.getRefundsByPaymentId(paymentId);
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<RefundDto>> getRefundsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RefundDto> refunds = refundService.getRefundsByUserId(userId, pageable);
        
        return ResponseEntity.ok(refunds);
    }
    
    @GetMapping
    public ResponseEntity<Page<RefundDto>> getRefundsByStatus(
            @RequestParam RefundStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<RefundDto> refunds = refundService.getRefundsByStatus(status, pageable);
        
        return ResponseEntity.ok(refunds);
    }
}
