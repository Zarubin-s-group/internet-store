package com.example.paymentservice.service.impl;

import com.example.paymentservice.dto.ErrorKafkaDto;
import com.example.paymentservice.dto.InventoryKafkaDto;
import com.example.paymentservice.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements KafkaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.error-order-service-topic}")
    private String kafkaErrorOrderServiceTopic;

    @Value("${spring.kafka.order-service-topic}")
    private String kafkaOrderServiceTopic;

    @Value("${spring.kafka.inventory-service-topic}")
    private String kafkaInventoryServiceTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(Object kafkaDto) {
        if (kafkaDto instanceof InventoryKafkaDto) {
            kafkaTemplate.send(kafkaInventoryServiceTopic, kafkaDto);
        } else if (kafkaDto instanceof ErrorKafkaDto) {
            kafkaTemplate.send(kafkaErrorOrderServiceTopic, kafkaDto);
        } else {
            kafkaTemplate.send(kafkaOrderServiceTopic, kafkaDto);
        }
        LOGGER.info("Sent message to Kafka -> '{}'", kafkaDto);
    }
}
