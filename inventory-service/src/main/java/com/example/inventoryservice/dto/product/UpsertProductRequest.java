package com.example.inventoryservice.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertProductRequest {

    @NotBlank(message = "Product title must not be blank")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @Positive(message = "Price per unit must be positive")
    private BigDecimal unitPrice;

    @Min(value = 0, message = "Quantity of product cannot be less than zero")
    private Integer count;

    @NotNull(message = "Category is required")
    private String categoryTitle;
}
