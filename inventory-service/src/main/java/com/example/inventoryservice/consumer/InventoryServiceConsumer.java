package com.example.inventoryservice.consumer;

import com.example.inventoryservice.dto.ErrorKafkaDto;
import com.example.inventoryservice.dto.InventoryKafkaDto;
import com.example.inventoryservice.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryServiceConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryServiceConsumer.class);

    private final ProductService productService;

    @Autowired
    public InventoryServiceConsumer(ProductService productService) {
        this.productService = productService;
    }

    @KafkaListener(topics = "${spring.kafka.inventory-service-topic}")
    public void consumeFromPaymentService(InventoryKafkaDto inventoryKafkaDto) {
        LOGGER.info("Consumed message from Kafka -> '{}'", inventoryKafkaDto);
        productService.checkProductAvailability(inventoryKafkaDto);
    }

    @KafkaListener(topics = "${spring.kafka.error-inventory-service-topic}",
            containerFactory = "errorKafkaListenerContainerFactory")
    public void consumeFromDeliveryService(ErrorKafkaDto errorKafkaDto) {
        LOGGER.info("Consumed an error message from Kafka -> '{}'", errorKafkaDto);
        productService.returnGoods(errorKafkaDto);
    }
}
