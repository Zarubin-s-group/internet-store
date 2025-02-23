package com.example.orderservice.repository;

import com.example.orderservice.domain.Order;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public interface OrderSpecifications {

    static Specification<Order> byUser(Long userId) {

        return(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) ->
                criteriaBuilder.equal(root.get("userId"), userId);
    }
}
