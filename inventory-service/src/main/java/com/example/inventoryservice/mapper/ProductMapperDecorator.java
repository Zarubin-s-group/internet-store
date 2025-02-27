package com.example.inventoryservice.mapper;

import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import com.example.inventoryservice.service.CategoryService;

public abstract class ProductMapperDecorator implements ProductMapper {

    @Override
    public Product requestToProduct(UpsertProductRequest request, CategoryService categoryService) {
        Product product = new Product();
        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setUnitPrice(request.getUnitPrice());
        product.setCount(request.getCount());
        product.setCategory(categoryService.getByTitle(request.getCategoryTitle()));

        return product;
    }

    @Override
    public Product requestToProduct(Long id, UpsertProductRequest request, CategoryService categoryService) {
        Product product = requestToProduct(request, categoryService);
        product.setId(id);

        return product;
    }
}
