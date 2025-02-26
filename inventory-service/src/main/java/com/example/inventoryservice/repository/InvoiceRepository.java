package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByOrderId(Long orderId);

    void deleteByOrderId(Long orderId);
}
