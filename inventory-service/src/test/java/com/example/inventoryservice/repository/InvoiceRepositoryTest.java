package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
public class InvoiceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvoiceRepository invoiceRepositoryJpa;

    @Test
    public void whenFindByOrderId_thenReturnInvoice() {

        Invoice invoice = new Invoice();
        invoice.setOrderId(1L);
        invoice.setProductCountMap(Collections.singletonMap(1L, 1));

        entityManager.persist(invoice);
        entityManager.flush();

        Invoice gotInvoice = invoiceRepositoryJpa.findByOrderId(invoice.getOrderId()).get();

        assertThat(gotInvoice.getId())
                .isEqualTo(invoice.getId());
    }

    @Test
    public void deleteByOrderId() {

        Invoice invoice = new Invoice();
        invoice.setOrderId(1L);
        invoice.setProductCountMap(Collections.singletonMap(1L, 1));

        entityManager.persist(invoice);
        entityManager.flush();

        Long invoiceId = invoice.getId();
        invoiceRepositoryJpa.deleteByOrderId(invoice.getOrderId());

        assertThat(invoiceRepositoryJpa.findById(invoiceId))
                .isEqualTo(Optional.empty());
    }
}
