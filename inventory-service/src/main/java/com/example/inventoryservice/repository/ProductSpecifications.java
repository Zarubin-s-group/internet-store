package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.product.ProductFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Objects;

public interface ProductSpecifications {

    static Specification<Product> withFilter(ProductFilter productFilter) {

        return Specification.where(byCategory(productFilter.getCategoryTitle()))
                .and((byPrice(productFilter.getMinPrice(), productFilter.getMaxPrice())));
    }

    static Specification<Product> byCategory(String categoryTitle) {

        if (Objects.isNull(categoryTitle)) return null;

        return(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            Join<Category, Product> productCategory = root.join("category");
            return criteriaBuilder.equal(productCategory.get("title"), categoryTitle);
        };
    }

    static Specification<Product> byPrice(BigDecimal minPrice, BigDecimal maxPrice) {

        if(minPrice == null && maxPrice == null) return null;

        if(minPrice == null) {
            return(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.le(root.get("unitPrice"), maxPrice);
        } else if(maxPrice == null) {
            return(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.ge(root.get("unitPrice"), minPrice);
        } else {
            return(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                    criteriaBuilder.between(root.get("unitPrice"), minPrice, maxPrice);
        }
    }
}
