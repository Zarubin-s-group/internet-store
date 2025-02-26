package com.example.inventoryservice.service;

import com.example.inventoryservice.config.TestConfig;
import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.dto.category.UpsertCategoryRequest;
import com.example.inventoryservice.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    private Category category;

    private List<Category> categories;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setTitle("some category");

        categories = Collections.singletonList(category);
    }

    @Test
    void getAll() {
        when(categoryService.getAll()).thenReturn(categories);
        assertDoesNotThrow(() -> categoryService.getAll());
    }

    @Test
    void whenExists_thanReturnCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        assertDoesNotThrow(() -> categoryService.getById(1L));
    }

    @Test
    void whenCategoryNotFound_thanException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> categoryService.getById(1L));
    }

    @Test
    public void createCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        assertDoesNotThrow(() -> categoryService.create(new UpsertCategoryRequest("some category")));
    }

    @Test
    public void updateCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        assertDoesNotThrow(() -> categoryService.update(1L, new UpsertCategoryRequest("some category")));
    }

    @Test
    public void whenCategoryNotFound_thanUpdateFailed() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () ->
                categoryService.update(1L, new UpsertCategoryRequest("some category")));
    }
}
