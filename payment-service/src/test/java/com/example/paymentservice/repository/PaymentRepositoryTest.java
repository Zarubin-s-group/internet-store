package com.example.paymentservice.repository;

import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class PaymentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PaymentRepository paymentRepositoryJpa;

    @Test
    public void whenGetByOrderId_thenReturnPayment() {
        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.ZERO);

        entityManager.persist(wallet);

        Payment payment = new Payment();
        payment.setOrderId(1L);
        payment.setCost(BigDecimal.TEN);
        payment.setWallet(wallet);

        entityManager.persist(payment);
        entityManager.flush();

        Payment gotPayment = paymentRepositoryJpa.findByOrderId(payment.getOrderId()).get();

        assertThat(gotPayment.getCost())
                .isEqualTo(payment.getCost());
    }
}
