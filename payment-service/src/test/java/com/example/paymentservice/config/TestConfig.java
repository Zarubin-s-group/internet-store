package com.example.paymentservice.config;

import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.repository.WalletRepository;
import com.example.paymentservice.service.KafkaService;
import com.example.paymentservice.service.PaymentService;
import com.example.paymentservice.service.WalletService;
import com.example.paymentservice.service.impl.PaymentServiceImpl;
import com.example.paymentservice.service.impl.WalletServiceImpl;
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
    public WalletRepository walletRepositoryMock(){
        return mock(WalletRepository.class);
    }

    @Bean
    public PaymentRepository paymentRepositoryMock() {
        return mock(PaymentRepository.class);
    }

    @Bean
    public WalletService walletService() {
        return new WalletServiceImpl(walletRepositoryMock());
    }

    @Bean
    public PaymentService paymentService() {
        return new PaymentServiceImpl(walletRepositoryMock(), paymentRepositoryMock(),
                kafkaServiceMock());
    }
}
