package com.example.paymentservice.service;

import com.example.paymentservice.config.TestConfig;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.*;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private PaymentKafkaDto paymentKafkaDto;

    private ErrorKafkaDto errorKafkaDto;

    @BeforeEach
    public void setUp() {
        paymentKafkaDto = new PaymentKafkaDto();
        paymentKafkaDto.setUserId(1L);
        paymentKafkaDto.setOrderId(1L);
        paymentKafkaDto.setTotalCost(BigDecimal.TEN);
        paymentKafkaDto.setDestinationAddress("some address");

        StatusDto statusDto = new StatusDto();
        statusDto.setComment("comment");
        statusDto.setStatus(OrderStatus.INVENTMENT_FAILED);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);

        errorKafkaDto = new ErrorKafkaDto(1L, statusDto);
    }

    @Test
    void pay() {
        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.TEN);

        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(wallet));
        assertDoesNotThrow(() -> paymentService.pay(paymentKafkaDto));
        assertThat(wallet.getBalance()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void payWithException() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> paymentService.pay(paymentKafkaDto));
    }

    @Test
    void resetPayment() {
        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.ZERO);

        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setCost(BigDecimal.TEN);
        payment.setWallet(wallet);

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));
        assertDoesNotThrow(() -> paymentService.resetPayment(errorKafkaDto));
        assertThat(wallet.getBalance()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void resetPaymentWithException() {
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> paymentService.resetPayment(errorKafkaDto));
    }
}
