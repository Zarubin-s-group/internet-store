package com.example.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentKafkaDto {

    private Long userId;

    private Long orderId;

    private List<OrderDetailsDto> orderDetails;

    private BigDecimal totalCost;

    private String destinationAddress;
}
