package com.example.inventoryservice.service;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.dto.category.UpsertCategoryRequest;

import java.util.List;

public interface CategoryService {

    List<Category> getAll();

    Category getById(Long id);

    Category getByTitle(String title);

    Category create (UpsertCategoryRequest request);

    Category update (Long id, UpsertCategoryRequest request);

    void deleteById(Long id);
}
