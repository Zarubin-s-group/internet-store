package com.example.inventoryservice.mapper;

import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.product.ProductListResponse;
import com.example.inventoryservice.dto.product.ProductResponse;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import com.example.inventoryservice.service.CategoryService;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@DecoratedWith(ProductMapperDecorator.class)
@Mapper(uses = CategoryMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product requestToProduct(UpsertProductRequest request, @Context CategoryService categoryService);

    Product requestToProduct(Long id, UpsertProductRequest request, @Context CategoryService categoryService);

    ProductResponse productToResponse(Product product);

    List<ProductResponse> productListToResponseList(List<Product> products);

    default ProductListResponse productListToProductListResponse(Page<Product> products) {
        return new ProductListResponse(products.getTotalElements(),productListToResponseList(products.getContent()));
    }
}
