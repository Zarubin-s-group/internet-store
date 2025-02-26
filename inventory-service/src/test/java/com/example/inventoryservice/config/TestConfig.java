package com.example.inventoryservice.config;

import com.example.inventoryservice.repository.CategoryRepository;
import com.example.inventoryservice.repository.InvoiceRepository;
import com.example.inventoryservice.repository.ProductRepository;
import com.example.inventoryservice.service.CategoryService;
import com.example.inventoryservice.service.KafkaService;
import com.example.inventoryservice.service.ProductService;
import com.example.inventoryservice.service.impl.CategoryServiceImpl;
import com.example.inventoryservice.service.impl.ProductServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public CategoryRepository categoryRepository() {
        return mock(CategoryRepository.class);
    }

    @Bean
    public ProductRepository productRepository() {
        return mock(ProductRepository.class);
    }

    @Bean
    public InvoiceRepository invoiceRepository() {
        return mock(InvoiceRepository.class);
    }

    @Bean
    public KafkaService kafkaService() {
        return mock(KafkaService.class);
    }

    @Bean
    public CategoryService categoryService() {
        return new CategoryServiceImpl(categoryRepository());
    }

    @Bean
    public ProductService productService() {
        return new ProductServiceImpl(productRepository(), categoryService(), kafkaService(), invoiceRepository());
    }
}
