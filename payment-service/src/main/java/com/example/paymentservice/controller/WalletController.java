package com.example.paymentservice.controller;

import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.ReplenishmentRequest;
import com.example.paymentservice.exception.AlreadyExistsException;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
public class WalletController {

    private final WalletService walletService;

    @Operation(summary = "Create an e-wallet", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/wallet")
    public ResponseEntity<Wallet> createWallet(HttpServletRequest request) throws AlreadyExistsException {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(walletService.createWallet(Long.valueOf(request.getHeader("id"))));
    }

    @Operation(summary = "Get balance", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/wallet/balance")
    public ResponseEntity<BigDecimal> getBalance(HttpServletRequest request) {

        return ResponseEntity.ok()
                .body(walletService.getBalance(Long.valueOf(request.getHeader("id"))));
    }

    @Operation(summary = "Replenish balance", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/wallet/balance/replenish")
    public ResponseEntity<BigDecimal> replenishBalance(HttpServletRequest request,
                                                       @Valid @RequestBody ReplenishmentRequest replenishmentRequest)
            throws EntityNotFoundException {

        walletService.replenishBalance(Long.valueOf(request.getHeader("id")), replenishmentRequest);
        return ResponseEntity.ok()
                .body(walletService.getBalance(Long.valueOf(request.getHeader("id"))));
    }
}
