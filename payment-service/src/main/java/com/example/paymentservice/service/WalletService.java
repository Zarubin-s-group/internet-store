package com.example.paymentservice.service;

import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.ReplenishmentRequest;

import java.math.BigDecimal;

public interface WalletService {

    Wallet createWallet(Long userId);

    BigDecimal getBalance(Long userId);

    BigDecimal replenishBalance(Long userId, ReplenishmentRequest replenishmentRequest);
}
