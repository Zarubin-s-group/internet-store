package com.example.inventoryservice.service;

import com.example.inventoryservice.config.TestConfig;
import com.example.inventoryservice.domain.Category;
import com.example.inventoryservice.domain.Invoice;
import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dto.product.ProductFilter;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import com.example.inventoryservice.repository.CategoryRepository;
import com.example.inventoryservice.repository.InvoiceRepository;
import com.example.inventoryservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class ProductServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ProductService productService;

    private Category category;

    private Product product;

    private List<Product> products;

    private UpsertProductRequest request;

    private InventoryKafkaDto inventoryKafkaDto;

    private Invoice invoice;

    private StatusDto statusDto;

    @BeforeEach
    public void setUp() {
        category = new Category();
        category.setTitle("any category");

        product = new Product();
        product.setId(1L);
        product.setTitle("some product");
        product.setDescription("description");
        product.setUnitPrice(BigDecimal.ONE);
        product.setCount(217);
        product.setCategory(category);

        products = Collections.singletonList(product);

        request = new UpsertProductRequest();
        request.setTitle("some product");
        request.setDescription("description");
        request.setUnitPrice(BigDecimal.ONE);
        request.setCount(217);
        request.setCategoryTitle("any category");

        OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setProductId(1L);
        orderDetailsDto.setCount(2);
        List<OrderDetailsDto> productList = new ArrayList<>();
        productList.add(orderDetailsDto);

        inventoryKafkaDto = new InventoryKafkaDto();
        inventoryKafkaDto.setUserId(1L);
        inventoryKafkaDto.setOrderId(1L);
        inventoryKafkaDto.setOrderDetails(productList);
        inventoryKafkaDto.setDestinationAddress("some address");

        invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(1L);
        invoice.setProductCountMap(Collections.singletonMap(1L, 2));

        statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.DELIVERY_FAILED);
        statusDto.setServiceName(ServiceName.DELIVERY_SERVICE);
        statusDto.setComment("comment");
    }

    @Test
    void getAll() {
        ProductFilter filter = new ProductFilter();
        when(productService.getAll(filter, PageRequest.of(0, 1))).thenReturn(new PageImpl<>(products));
        assertDoesNotThrow(() -> productService.getAll(filter, Pageable.ofSize(1)));
    }

    @Test
    void whenExists_thanReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        assertDoesNotThrow(() -> productService.getById(1L));
    }

    @Test
    void whenProductNotFound_thanException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.getById(1L));
    }

    @Test
    public void createProduct() {
        when(categoryRepository.findByTitle("any category")).thenReturn(Optional.ofNullable(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        assertDoesNotThrow(() -> productService.create(request));
    }

    @Test
    public void updateProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(product));
        when(categoryRepository.findByTitle("any category")).thenReturn(Optional.ofNullable(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        assertDoesNotThrow(() -> productService.update(1L, request));
    }

    @Test
    public void whenProductNotFound_thanUpdateFailed() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.update(1L, request));
    }

    @Test
    void checkProductAvailability() {
        when(invoiceRepository.saveAndFlush(any(Invoice.class))).thenReturn(invoice);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.checkProductAvailability(inventoryKafkaDto);
        assertThat(product.getCount()).isEqualTo(215);
    }

    @Test
    void checkProductAvailabilityWithException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.checkProductAvailability(inventoryKafkaDto));
    }

    @Test
    void returnGoods() {
        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto(1L, statusDto);

        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.of(invoice));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        productService.returnGoods(errorKafkaDto);
        assertThat(product.getCount()).isEqualTo(219);
        verify(invoiceRepository, times(1)).deleteByOrderId(1L);
    }

    @Test
    void returnGoodsWithException() {
        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto(2L, statusDto);

        when(invoiceRepository.findByOrderId(2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> productService.returnGoods(errorKafkaDto));
    }
}