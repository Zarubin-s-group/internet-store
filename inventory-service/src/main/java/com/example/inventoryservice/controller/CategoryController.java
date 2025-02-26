package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.category.CategoryListResponse;
import com.example.inventoryservice.dto.category.CategoryResponse;
import com.example.inventoryservice.dto.category.UpsertCategoryRequest;
import com.example.inventoryservice.mapper.CategoryMapper;
import com.example.inventoryservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Get all categories")
    @GetMapping("/view")
    public ResponseEntity<CategoryListResponse> getAllCategories() {
        return ResponseEntity.ok(CategoryMapper.INSTANCE.categoryListToCategoryListResponse(categoryService.getAll()));
    }

    @Operation(summary = "Get category by id")
    @GetMapping("/view/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryMapper.INSTANCE.categoryToResponse(categoryService.getById(id)));
    }

    @Operation(summary = "Add new category", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> createCategory(UpsertCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryMapper.INSTANCE.categoryToResponse(categoryService.create(request)));
    }

    @Operation(summary = "Update category", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody UpsertCategoryRequest request) {
        return ResponseEntity.ok(CategoryMapper.INSTANCE.categoryToResponse(categoryService.update(id, request)));
    }

    @Operation(summary = "Delete category", security = @SecurityRequirement(name = "bearerAuth"))
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable Long id) {
        categoryService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
