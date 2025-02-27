package com.example.paymentservice.repository;

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
public class WalletRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WalletRepository walletRepositoryJpa;

    @Test
    public void whenGetByUserId_thenReturnWallet() {

        Wallet wallet = new Wallet();
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.ONE);

        entityManager.persistAndFlush(wallet);

        Wallet gotWallet = walletRepositoryJpa.findByUserId(wallet.getUserId()).get();

        assertThat(gotWallet.getBalance())
                .isEqualTo(wallet.getBalance());
    }
}
