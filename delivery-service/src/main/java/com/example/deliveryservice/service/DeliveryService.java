package com.example.deliveryservice.service;

import com.example.deliveryservice.dto.DeliveryKafkaDto;

public interface DeliveryService {

    void deliver(DeliveryKafkaDto deliveryKafkaDto);
}
