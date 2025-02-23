package com.example.orderservice.service.impl;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.ServiceName;
import com.example.orderservice.dto.OrderDetailsDto;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.PaymentKafkaDto;
import com.example.orderservice.dto.StatusDto;
import com.example.orderservice.exception.OrderNotFoundException;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OrderSpecifications;
import com.example.orderservice.service.KafkaService;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final KafkaService kafkaService;

    @Override
    public Page<Order> getAllOrders(Long userId, boolean isAdmin, Pageable pageable) {
        Specification<Order> spec = isAdmin ? null : OrderSpecifications.byUser(userId);
        return orderRepository.findAll(spec, pageable);
    }

    @Override
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    @Override
    public Optional<Order> addOrder(Long userId, CreateOrderRequest createOrderRequest) {
        List<OrderDetailsDto> orderDetails = createOrderRequest.getOrderDetails();
        Map<Long, Integer> productQuantityMap = createOrderRequest.getOrderDetails()
                .stream()
                .collect(Collectors.toMap(OrderDetailsDto::getProductId, OrderDetailsDto::getCount));
        Order newOrder = new Order(
                createOrderRequest.getDescription(),
                createOrderRequest.getDestinationAddress(),
                createOrderRequest.getTotalCost(),
                OrderStatus.REGISTERED
        );
        newOrder.setUserId(userId);
        newOrder.addOrderDetails(productQuantityMap);
        newOrder.addStatusHistory(newOrder.getStatus(), ServiceName.ORDER_SERVICE, "Order created");
        Order order = orderRepository.saveAndFlush(newOrder);
        log.info("Order with id {} was registered at {}", order.getId(),order.getCreationTime());

        kafkaService.produce(PaymentKafkaDto.builder()
                .userId(userId)
                .orderId(order.getId())
                .orderDetails(orderDetails)
                .totalCost(order.getTotalCost())
                .destinationAddress(order.getDestinationAddress())
                .build());
        return Optional.of(order);
    }

    @Transactional
    @Override
    public void updateOrderStatus(Long id, StatusDto statusDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        if (order.getStatus() == statusDto.getStatus()) {
            log.info("Request with same status {} for order {} from service {}", statusDto.getStatus(), id, statusDto.getServiceName());
            return;
        }
        order.setStatus(statusDto.getStatus());
        order.addStatusHistory(statusDto.getStatus(), statusDto.getServiceName(), statusDto.getComment());
        orderRepository.save(order);
        log.info("Status for order with id {} changed to: {}", id, order.getStatus());
    }
}