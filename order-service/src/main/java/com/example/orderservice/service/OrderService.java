package com.example.orderservice.service;

import com.example.orderservice.domain.Order;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.StatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {

    Page<Order> getAllOrders(Long userId, boolean isAdmin, Pageable pageable);

    Order getOrder(Long id);

    Optional<Order> addOrder(Long userId, CreateOrderRequest createOrderRequest);

    void updateOrderStatus(Long id, StatusDto statusDto);
}
