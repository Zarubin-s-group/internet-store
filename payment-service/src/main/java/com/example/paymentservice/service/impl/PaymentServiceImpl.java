package com.example.paymentservice.service.impl;

import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.Wallet;
import com.example.paymentservice.dto.*;
import com.example.paymentservice.exception.EntityNotFoundException;
import com.example.paymentservice.exception.InsufficientFundsException;
import com.example.paymentservice.repository.PaymentRepository;
import com.example.paymentservice.repository.WalletRepository;
import com.example.paymentservice.service.KafkaService;
import com.example.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {

    private final WalletRepository walletRepository;

    private final PaymentRepository paymentRepository;

    private final KafkaService kafkaService;

    @Transactional
    @Override
    public void pay(PaymentKafkaDto paymentKafkaDto) {
        try {
            Thread.sleep(3000);
            Long userId = paymentKafkaDto.getUserId();
            Long orderId = paymentKafkaDto.getOrderId();
            BigDecimal cost = paymentKafkaDto.getTotalCost();

            Optional<Wallet> optionalWallet = walletRepository.findByUserId(userId);
            if (optionalWallet.isEmpty()) {
                String comment = MessageFormat.format("Wallet for user with id {0} not found",
                        userId);
                StatusDto statusDto = createStatusDto(OrderStatus.PAYMENT_FAILED, comment);
                kafkaService.produce(new ErrorKafkaDto(orderId, statusDto));

                throw new EntityNotFoundException(comment);
            }

            Wallet wallet = optionalWallet.get();
            if (wallet.getBalance().compareTo(cost) < 0) {
                String comment = "Insufficient funds";
                StatusDto statusDto = createStatusDto(OrderStatus.PAYMENT_FAILED, comment);
                kafkaService.produce(new ErrorKafkaDto(orderId, statusDto));

                throw new InsufficientFundsException(comment);
            }

            recordFactOfPayment(orderId, cost, wallet);

            String comment = "Order paid";
            StatusDto statusDto = createStatusDto(OrderStatus.PAID, comment);
            kafkaService.produce((InventoryKafkaDto.builder()
                    .userId(userId))
                    .orderId(orderId)
                    .orderDetails(paymentKafkaDto.getOrderDetails())
                    .destinationAddress(paymentKafkaDto.getDestinationAddress())
                    .build());
            kafkaService.produce(new OrderKafkaDto(orderId, statusDto));

        } catch (Exception ex) {
            if (!(ex instanceof EntityNotFoundException) && !(ex instanceof InsufficientFundsException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(new ErrorKafkaDto(paymentKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void resetPayment(ErrorKafkaDto errorKafkaDto) {
        try {
            Optional<Payment> optionalPayment
                    = paymentRepository.findByOrderId(errorKafkaDto.getOrderId());

            if (optionalPayment.isEmpty()) {
                throw new EntityNotFoundException(MessageFormat.format("Payment for order with id {0} not found",
                        errorKafkaDto.getOrderId()));
            }

            Payment payment = optionalPayment.get();
            Wallet wallet = payment.getWallet();
            wallet.setBalance(wallet.getBalance().add(payment.getCost()));
            walletRepository.save(wallet);
            paymentRepository.delete(payment);
            log.info("Payment for order with id {} has been cancelled. The account balance is {}.",
                    errorKafkaDto.getOrderId(), wallet.getBalance());

            kafkaService.produce(errorKafkaDto);

        } catch (Exception ex) {
            StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
            kafkaService.produce(new ErrorKafkaDto(errorKafkaDto.getOrderId(), statusDto));

            throw new RuntimeException(ex.getMessage());
        }
    }

    private void recordFactOfPayment(Long orderId, BigDecimal cost, Wallet wallet) {
        wallet.setBalance(wallet.getBalance().subtract(cost));
        walletRepository.save(wallet);

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setCost(cost);
        payment.setWallet(wallet);
        paymentRepository.save(payment);
        log.info("Order with id {} has been paid. The account balance is {}.", orderId, wallet.getBalance());
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.PAYMENT_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }
}
