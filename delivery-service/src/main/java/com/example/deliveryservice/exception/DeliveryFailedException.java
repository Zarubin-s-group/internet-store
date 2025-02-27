package com.example.deliveryservice.exception;

public class DeliveryFailedException extends RuntimeException {

    public DeliveryFailedException(String message) {
        super(message);
    }
}
