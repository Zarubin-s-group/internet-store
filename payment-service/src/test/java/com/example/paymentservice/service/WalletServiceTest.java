package com.example.paymentservice.service;

import com.example.paymentservice.config.TestConfig;
import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.ReplenishmentRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    Wallet wallet;

    @BeforeEach
    public void setUp() {
        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.TEN);
    }

    @Test
    void createWallet() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertDoesNotThrow(() -> walletService.createWallet(1L));
    }

    @Test
    void whenWalletAlreadyExists_thanException() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.ofNullable(wallet));
        assertThrows(RuntimeException.class, () -> walletService.createWallet(1L));
    }

    @Test
    void replenishBalance() {
        ReplenishmentRequest replenishmentRequest = new ReplenishmentRequest(BigDecimal.ONE);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.ofNullable(wallet));
        assertEquals(new BigDecimal(11), walletService.replenishBalance(1L, replenishmentRequest));
    }

    @Test
    void whenWalletNotFound_thanReplenishmentIsFailed() {
        ReplenishmentRequest replenishmentRequest = new ReplenishmentRequest(BigDecimal.ONE);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> walletService.replenishBalance(1L, replenishmentRequest));
    }

    @Test
    void getBalance() {
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.ofNullable(wallet));
        assertEquals(BigDecimal.TEN, walletService.getBalance(1L));
    }
}