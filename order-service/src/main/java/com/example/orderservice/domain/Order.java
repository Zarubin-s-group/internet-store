package com.example.orderservice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "description")
    private String description;

    @Column(name = "destination_address")
    private String destinationAddress;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderDetails> orderDetails = new ArrayList<>();

    @Column(name = "total_cost")
    private BigDecimal totalCost;

    @CreationTimestamp
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @UpdateTimestamp
    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderStatusHistory> orderStatusHistory = new ArrayList<>();

    public Order(
            String description,
            String destinationAddress,
            List<OrderDetails> orderDetails,
            BigDecimal totalCost,
            OrderStatus status
    ) {
        this.description = description;
        this.destinationAddress = destinationAddress;
        this.orderDetails = orderDetails;
        this.totalCost = totalCost;
        this.status = status;
    }

    public Order(
            String description,
            String destinationAddress,
            BigDecimal totalCost,
            OrderStatus status
    ) {
        this.description = description;
        this.destinationAddress = destinationAddress;
        this.totalCost = totalCost;
        this.status = status;
    }

    public void addOrderDetails(Map<Long, Integer> productQuantityMap) {
        List<OrderDetails> orderDetails = new ArrayList<>();
        productQuantityMap.forEach((key, value) ->
                orderDetails.add(new OrderDetails(null, this, key, value)));

        getOrderDetails().addAll(orderDetails);
    }

    public void addStatusHistory(OrderStatus status, ServiceName serviceName, String comment) {
        getOrderStatusHistory().add(new OrderStatusHistory(null, status, serviceName, comment, this));
    }
}
