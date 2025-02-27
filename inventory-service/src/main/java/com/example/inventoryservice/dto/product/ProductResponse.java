package com.example.inventoryservice.dto.product;

import com.example.inventoryservice.dto.category.CategoryResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String title;

    private String description;

    private BigDecimal unitPrice;

    private Integer count;

    private CategoryResponse category;
}
