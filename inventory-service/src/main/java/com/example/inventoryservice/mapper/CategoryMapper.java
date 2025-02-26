package com.example.inventoryservice.mapper;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.dto.category.CategoryListResponse;
import com.example.inventoryservice.dto.category.CategoryResponse;
import com.example.inventoryservice.dto.category.UpsertCategoryRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    Category requestToCategory(UpsertCategoryRequest request);

    void update(UpsertCategoryRequest request, @MappingTarget Category category);

    CategoryResponse categoryToResponse(Category category);

    List<CategoryResponse> categoryListToResponseList(List<Category> categories);

    default CategoryListResponse categoryListToCategoryListResponse(List<Category> categories) {
        CategoryListResponse response = new CategoryListResponse();
        response.setCategories(categoryListToResponseList(categories));
        return response;
    }
}
