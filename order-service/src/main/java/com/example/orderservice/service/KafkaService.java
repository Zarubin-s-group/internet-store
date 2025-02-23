package com.example.orderservice.service;

import com.example.orderservice.dto.PaymentKafkaDto;

public interface KafkaService {

    void produce(PaymentKafkaDto paymentKafkaDto);
}
