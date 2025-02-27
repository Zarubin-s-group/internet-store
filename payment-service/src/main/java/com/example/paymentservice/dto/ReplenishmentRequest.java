package com.example.paymentservice.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplenishmentRequest {

    @Min(value = 1, message = "The amount for replenishment cannot be less than 1")
    private BigDecimal amount;
}
