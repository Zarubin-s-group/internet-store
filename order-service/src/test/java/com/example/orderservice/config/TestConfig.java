package com.example.orderservice.config;

import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.KafkaService;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.service.impl.OrderServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public KafkaService kafkaServiceMock() {
        return mock(KafkaService.class);
    }

    @Bean
    public OrderRepository orderRepositoryMock(){
        return mock(OrderRepository.class);
    }

    @Bean
    public OrderService orderServiceTest() {
        return new OrderServiceImpl(orderRepositoryMock(), kafkaServiceMock());
    }
}
