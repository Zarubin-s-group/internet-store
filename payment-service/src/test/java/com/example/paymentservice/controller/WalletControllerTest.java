package com.example.paymentservice.controller;

import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.ReplenishmentRequest;
import com.example.paymentservice.exception.AlreadyExistsException;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Configuration
    @ComponentScan(basePackageClasses = {WalletController.class})
    public static class TestConf {
    }

    @Test
    void createWallet() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.ZERO);

        when(walletService.createWallet(1L)).thenReturn(wallet);
        mockMvc.perform(
                        post("/wallet")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1));
    }

    @Test
    void createWalletWithException() throws Exception {
        doThrow(new AlreadyExistsException("Wallet for user with id {0} already exists"))
                .when(walletService).createWallet(1L);
        mockMvc.perform(
                        post("/wallet")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void replenishBalance() throws Exception {
        ReplenishmentRequest replenishmentRequest = new ReplenishmentRequest(BigDecimal.TEN);

        when(walletService.replenishBalance(1L, replenishmentRequest)).thenReturn(BigDecimal.TEN);
        mockMvc.perform(
                        patch("/wallet/balance/replenish")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(replenishmentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }

    @Test
    void replenishBalanceWithException() throws Exception {
        ReplenishmentRequest replenishmentRequest = new ReplenishmentRequest(BigDecimal.TEN);

        doThrow(new EntityNotFoundException("Wallet for user with id 1 not found"))
                .when(walletService).replenishBalance(1L, replenishmentRequest);
        mockMvc.perform(
                        patch("/wallet/balance/replenish")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                })
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(replenishmentRequest))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getBalance() throws Exception {
        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUserId(1L);
        wallet.setBalance(BigDecimal.TEN);

        when(walletService.getBalance(1L)).thenReturn(Optional.of(wallet).get().getBalance());
        mockMvc.perform(
                        get("/wallet/balance")
                                .with(request -> {
                                    request.addHeader("id", 1L);
                                    return request;
                                }))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(wallet.getBalance().toString())));
    }
}
