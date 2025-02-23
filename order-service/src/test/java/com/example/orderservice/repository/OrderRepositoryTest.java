package com.example.orderservice.repository;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import com.example.orderservice.domain.ServiceName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepositoryJpa;

    @Test
    public void whenGetByDescription_thenReturnOrder() {
        Order order = new Order(
                "Order #112",
                "Moscow, st.Taganskaya 150",
                BigDecimal.ONE,
                OrderStatus.REGISTERED
        );
        order.addStatusHistory(OrderStatus.REGISTERED, ServiceName.ORDER_SERVICE, "Order created");
        entityManager.persist(order);
        entityManager.flush();

        String desc = order.getDescription();
        Order gotOrder = orderRepositoryJpa.findByDescription(desc).get();

        assertThat(gotOrder.getStatus())
                .isEqualTo(order.getStatus());
        assertThat(gotOrder.getOrderStatusHistory().size())
                .isEqualTo(1);

        // test order status change
        gotOrder.setStatus(OrderStatus.PAID);
        gotOrder.addStatusHistory(OrderStatus.PAID, ServiceName.ORDER_SERVICE, "Order paid");
        orderRepositoryJpa.save(gotOrder);

        gotOrder = orderRepositoryJpa.findByDescription(desc).get();
        assertThat(gotOrder.getStatus())
                .isEqualTo(OrderStatus.PAID);
        assertThat(orderRepositoryJpa.findOrderStatusHistoryById(gotOrder.getId()).size())
                .isEqualTo(2);
    }
}
