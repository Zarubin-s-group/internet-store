package com.example.orderservice.service;

import com.example.orderservice.config.TestConfig;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderDetailsDto;
import com.example.orderservice.dto.StatusDto;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.repository.OrderSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    private List<Order> orders;

    @BeforeEach
    public void setUp() {
        order = new Order(
                "Order #112",
                "Moscow, st.Taganskaya 150",
                BigDecimal.ONE,
                OrderStatus.REGISTERED
        );
        order.setId(1L);
        order.setUserId(1L);
        order.setCreationTime(LocalDateTime.now());
        order.setModifiedTime(LocalDateTime.now());
        order.addOrderDetails(new HashMap<>(Collections.singletonMap(1L, 1)));

        orders = Collections.singletonList(order);
    }

    @Test
    void getAllOrders() {
        when(orderRepository.findAll(OrderSpecifications.byUser(1L), Pageable.ofSize(1)))
                .thenReturn(new PageImpl<>(orders));
        assertDoesNotThrow(() -> orderService.getAllOrders(1L, false, Pageable.ofSize(1)));
    }

    @Test
    void whenOrderExists_thenReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(order));
        assertDoesNotThrow(() -> orderService.getOrder(1L));
    }

    @Test
    void whenOrderNotFound_thenException() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void addOrder() {
        OrderDetailsDto orderDetailsDto = new OrderDetailsDto(1L, 1);
        CreateOrderRequest orderDto = new CreateOrderRequest(
                "Order #112",
                "Moscow, st.Taganskaya 150",
                List.of(orderDetailsDto),
                BigDecimal.ONE
        );
        when(orderRepository.saveAndFlush(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.addOrder(1L, orderDto));
    }

    @Test
    void updateOrderStatus() {
        when(orderRepository.findById(1L)).thenReturn(Optional.ofNullable(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        assertDoesNotThrow(() -> orderService.updateOrderStatus(1L, new StatusDto(OrderStatus.PAID)));
    }

    @Test
    void updateOrderStatusWithError() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                orderService.updateOrderStatus(1L, new StatusDto(OrderStatus.PAID)));
    }
}
