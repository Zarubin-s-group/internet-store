package com.example.inventoryservice.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpsertCategoryRequest {

    @NotBlank(message = "Category title must not be blank")
    private String title;
}
