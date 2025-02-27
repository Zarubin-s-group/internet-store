package com.example.orderservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;

    private Long userId;

    private String description;

    private String destinationAddress;

    private List<OrderDetailsDto> orderDetails;

    private BigDecimal totalCost;

    private List<StatusDto> orderStatusHistory;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
