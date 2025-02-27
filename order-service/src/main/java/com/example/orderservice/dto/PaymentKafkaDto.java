package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentKafkaDto {

    private Long userId;

    private Long orderId;

    private List<OrderDetailsDto> orderDetails;

    private BigDecimal totalCost;

    private String destinationAddress;
}
