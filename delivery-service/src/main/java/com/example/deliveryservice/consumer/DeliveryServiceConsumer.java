package com.example.deliveryservice.consumer;

import com.example.deliveryservice.dto.DeliveryKafkaDto;
import com.example.deliveryservice.service.DeliveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryServiceConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryServiceConsumer.class);

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryServiceConsumer(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @KafkaListener(topics = "${spring.kafka.delivery-service-topic}")
    public void consumeFromInventoryService(DeliveryKafkaDto deliveryKafkaDto) {
        LOGGER.info("Consumed message from Kafka -> '{}'", deliveryKafkaDto);
        deliveryService.deliver(deliveryKafkaDto);
    }
}
