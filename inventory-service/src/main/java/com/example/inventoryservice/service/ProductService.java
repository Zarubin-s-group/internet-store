package com.example.inventoryservice.service;

import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.ErrorKafkaDto;
import com.example.inventoryservice.dto.InventoryKafkaDto;
import com.example.inventoryservice.dto.product.ProductFilter;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<Product> getAll(ProductFilter productFilter, Pageable pageable);

    Product getById(Long id);

    Product create(UpsertProductRequest request);

    Product update(long id, UpsertProductRequest request);

    void deleteById(Long id);

    void checkProductAvailability(InventoryKafkaDto inventoryKafkaDto);

    void returnGoods(ErrorKafkaDto errorKafkaDto);
}
