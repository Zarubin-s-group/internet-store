package com.example.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryKafkaDto {

    private Long userId;

    private Long orderId;

    private List<OrderDetailsDto> orderDetails;

    private String destinationAddress;
}