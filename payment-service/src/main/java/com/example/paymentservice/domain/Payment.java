package com.example.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "cost")
    private BigDecimal cost;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @CreationTimestamp
    @Column(name = "creation_time")
    private LocalDateTime creationTime;
}
