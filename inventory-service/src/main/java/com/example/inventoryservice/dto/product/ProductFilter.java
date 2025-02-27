package com.example.inventoryservice.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {

    private String categoryTitle;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;
}
