package com.example.inventoryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryKafkaDto {

    private Long userId;

    private Long orderId;

    private List<OrderDetailsDto> orderDetails;

    private String destinationAddress;
}
