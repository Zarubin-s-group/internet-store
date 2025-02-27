package com.example.inventoryservice.service.impl;

import com.example.inventoryservice.domain.Invoice;
import com.example.inventoryservice.domain.Product;
import com.example.inventoryservice.dto.*;
import com.example.inventoryservice.dto.product.ProductFilter;
import com.example.inventoryservice.dto.product.UpsertProductRequest;
import com.example.inventoryservice.exception.EntityNotFoundException;
import com.example.inventoryservice.exception.NotEnoughProductException;
import com.example.inventoryservice.mapper.ProductMapper;
import com.example.inventoryservice.repository.InvoiceRepository;
import com.example.inventoryservice.repository.ProductRepository;
import com.example.inventoryservice.repository.ProductSpecifications;
import com.example.inventoryservice.service.CategoryService;
import com.example.inventoryservice.service.KafkaService;
import com.example.inventoryservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    private final KafkaService kafkaService;

    private final InvoiceRepository invoiceRepository;

    @Override
    public Page<Product> getAll(ProductFilter productFilter, Pageable pageable) {
        return productRepository.findAll(ProductSpecifications.withFilter(productFilter), pageable);
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(MessageFormat.format("Product with id {0} not found", id)));
    }

    @Override
    public Product create(UpsertProductRequest request) {
        return productRepository.save(ProductMapper.INSTANCE.requestToProduct(request, categoryService));
    }

    @Override
    public Product update(long id, UpsertProductRequest request) {
        if(productRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(MessageFormat.format("Product with id {0} not found", id));
        }
        return productRepository.save(ProductMapper.INSTANCE.requestToProduct(id, request, categoryService));
    }

    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void checkProductAvailability(InventoryKafkaDto inventoryKafkaDto) {
        try {
            Thread.sleep(3000);
            Long orderId = inventoryKafkaDto.getOrderId();
            Map<Long, Integer> orderMap = inventoryKafkaDto.getOrderDetails()
                    .stream()
                    .collect(Collectors.toMap(OrderDetailsDto::getProductId, OrderDetailsDto::getCount));

            Invoice invoice = new Invoice();
            invoice.setOrderId(orderId);
            invoice.setProductCountMap(orderMap);
            Invoice savedInvoice = invoiceRepository.saveAndFlush(invoice);

            assembleOrder(orderMap, orderId);

            String comment = "Goods are assembled and ready for delivery";
            StatusDto statusDto = createStatusDto(OrderStatus.INVENTED, comment);
            kafkaService.produce(DeliveryKafkaDto.builder()
                    .orderId(orderId)
                    .invoiceId(savedInvoice.getId())
                    .destinationAddress(inventoryKafkaDto.getDestinationAddress())
                    .build());
            kafkaService.produce(new OrderKafkaDto(orderId, statusDto));

        } catch (Exception ex) {
            if (!(ex instanceof EntityNotFoundException) && !(ex instanceof NotEnoughProductException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(new ErrorKafkaDto(inventoryKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void returnGoods(ErrorKafkaDto errorKafkaDto) {
        try {
            Long orderId = errorKafkaDto.getOrderId();
            Invoice invoice = invoiceRepository.findByOrderId(orderId).orElseThrow(() ->
                    new EntityNotFoundException(MessageFormat.format("Invoice for order with id {0} not found", orderId)));
            Map<Long, Integer> orderMap
                    = invoice.getProductCountMap();

            List<Product> updatedInventoryList = new ArrayList<>();
            orderMap.keySet().forEach(k -> {
                Product product = productRepository.findById(k).orElseThrow();
                product.setCount(product.getCount() + orderMap.get(k));
                updatedInventoryList.add(product);
            });

            productRepository.saveAll(updatedInventoryList);
            invoiceRepository.deleteByOrderId(orderId);

            kafkaService.produce(errorKafkaDto);

        } catch (Exception ex) {
            StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
            kafkaService.produce(new ErrorKafkaDto(errorKafkaDto.getOrderId(), statusDto));

            throw new RuntimeException(ex.getMessage());
        }
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }

    private void assembleOrder(Map<Long, Integer> orderMap, Long orderId) throws EntityNotFoundException {

        for (Map.Entry<Long, Integer> entry : orderMap.entrySet()) {
            Long productId = entry.getKey();
            Integer requiredQuantity = entry.getValue();

            Optional<Product> product = productRepository.findById(productId);
            if (product.isEmpty()) {
                String comment = MessageFormat.format("Product with id {0} not found.", productId);
                StatusDto statusDto = createStatusDto(OrderStatus.INVENTMENT_FAILED, comment);
                kafkaService.produce(new ErrorKafkaDto(orderId, statusDto));

                throw new EntityNotFoundException(comment);
            }

            Integer availableQuantity = product.get().getCount();
            if (availableQuantity < requiredQuantity) {
                String comment = MessageFormat.format("Insufficient product with id {0}.", productId);
                StatusDto statusDto = createStatusDto(OrderStatus.INVENTMENT_FAILED, comment);
                kafkaService.produce(new ErrorKafkaDto(orderId, statusDto));

                throw new NotEnoughProductException(comment);
            }

            availableQuantity = availableQuantity - requiredQuantity;
            product.get().setCount(availableQuantity);
            productRepository.save(product.get());
        }
    }
}
