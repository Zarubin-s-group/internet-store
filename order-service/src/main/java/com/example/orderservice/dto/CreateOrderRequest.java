package com.example.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    private String description;

    @NotBlank(message = "Destination address is required")
    private String destinationAddress;

    @NotEmpty(message = "Product list must not be empty")
    private List<OrderDetailsDto> orderDetails;

    @Positive(message = "Cost must be positive")
    private BigDecimal totalCost;
}
