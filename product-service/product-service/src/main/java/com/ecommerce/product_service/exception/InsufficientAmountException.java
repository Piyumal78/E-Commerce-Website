package com.ecommerce.product_service.exception;

public class InsufficientAmountException extends Exception {
    public InsufficientAmountException(String message) {
        super(message);
    }
}
