package com.example.deliveryservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryKafkaDto {

    private Long orderId;

    private Long invoiceId;

    private String destinationAddress;
}
