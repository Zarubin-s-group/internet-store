package com.example.orderservice.exception;

import java.text.MessageFormat;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long orderId) {
        super(MessageFormat.format("Order with id {0} not found", orderId));
    }
}
