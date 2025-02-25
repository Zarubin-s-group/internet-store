package com.example.paymentservice.consumer;

import com.example.paymentservice.dto.ErrorKafkaDto;
import com.example.paymentservice.dto.PaymentKafkaDto;
import com.example.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentServiceConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceConsumer.class);

    private final PaymentService paymentService;

    @Autowired
    public PaymentServiceConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "${spring.kafka.payment-service-topic}")
    public void consumeFromOrderService(PaymentKafkaDto paymentKafkaDto) {
        LOGGER.info("Consumed message from Kafka -> '{}'", paymentKafkaDto);
        paymentService.pay(paymentKafkaDto);
    }

    @KafkaListener(topics = "${spring.kafka.error-payment-service-topic}",
            containerFactory = "ErrorKafkaListenerContainerFactory")
    public void consumeFromInventoryService(ErrorKafkaDto errorKafkaDto) {
        LOGGER.info("Consumed an error message from Kafka -> '{}'", errorKafkaDto);
        paymentService.resetPayment(errorKafkaDto);
    }
}
