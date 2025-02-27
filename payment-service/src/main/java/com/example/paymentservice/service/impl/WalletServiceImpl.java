package com.example.paymentservice.service.impl;

import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.ReplenishmentRequest;
import com.example.paymentservice.exception.AlreadyExistsException;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.repository.WalletRepository;
import com.example.paymentservice.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public Wallet createWallet(Long userId) throws AlreadyExistsException {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        if (optionalWallet.isPresent()) {
            throw new AlreadyExistsException(MessageFormat.format("Wallet for user with id {0} already exists",
                    userId));
        }

        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        return walletRepository.save(wallet);
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("Wallet for user with id {0} not found",
                    userId));
        }

        Wallet wallet = optionalWallet.get();
        return wallet.getBalance();
    }

    @Override
    public BigDecimal replenishBalance(Long userId, ReplenishmentRequest replenishmentRequest) {
        Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
        if (optionalWallet.isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("Wallet for user with id {0} not found",
                    userId));
        }

        Wallet wallet = optionalWallet.get();
        wallet.setBalance(wallet.getBalance().add(replenishmentRequest.getAmount()));
        walletRepository.save(wallet);

        return wallet.getBalance();
    }
}
