package com.example.deliveryservice.service.impl;

import com.example.deliveryservice.domain.Delivery;
import com.example.deliveryservice.dto.*;
import com.example.deliveryservice.exception.DeliveryFailedException;
import com.example.deliveryservice.repository.DeliveryRepository;
import com.example.deliveryservice.service.DeliveryService;
import com.example.deliveryservice.service.KafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;

    private final KafkaService kafkaService;

    @Transactional
    @Override
    public void deliver(DeliveryKafkaDto deliveryKafkaDto) {
        try {
            Thread.sleep(3000);
            Long orderId = deliveryKafkaDto.getOrderId();
            Long invoiceId = deliveryKafkaDto.getInvoiceId();

            Delivery delivery = new Delivery();
            delivery.setInvoiceId(invoiceId);
            delivery.setDestinationAddress(deliveryKafkaDto.getDestinationAddress());
            deliveryRepository.save(delivery);

            double randomValue = Math.round(Math.random() * 100.0) / 100.0;
            if (randomValue > 0.85) {
                String comment = "Delivery failed";
                StatusDto statusDto = createStatusDto(OrderStatus.DELIVERY_FAILED, comment);
                kafkaService.produce(new ErrorKafkaDto(orderId, statusDto));

                throw new DeliveryFailedException(comment);
            }

            String comment = "Delivery completed";
            StatusDto statusDto = createStatusDto(OrderStatus.DELIVERED, comment);
            kafkaService.produce(new OrderKafkaDto(orderId, statusDto));

        } catch (Exception ex) {
            if (!(ex instanceof DeliveryFailedException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(new ErrorKafkaDto(deliveryKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.DELIVERY_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }
}
