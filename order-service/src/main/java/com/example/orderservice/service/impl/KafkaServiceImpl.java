package com.example.orderservice.service.impl;

import com.example.orderservice.dto.PaymentKafkaDto;
import com.example.orderservice.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaServiceImpl implements KafkaService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);

    @Value("${spring.kafka.payment-service-topic}")
    private String kafkaTopic;

    private final KafkaTemplate<String, PaymentKafkaDto> kafkaTemplate;

    public KafkaServiceImpl(KafkaTemplate<String, PaymentKafkaDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(PaymentKafkaDto paymentKafkaDto) {
        kafkaTemplate.send(kafkaTopic, paymentKafkaDto);
        logger.info("Sent message to Kafka -> '{}'", paymentKafkaDto);
    }
}
