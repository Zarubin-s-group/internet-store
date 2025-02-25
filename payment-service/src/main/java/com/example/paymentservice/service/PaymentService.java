package com.example.paymentservice.service;

import com.example.paymentservice.dto.ErrorKafkaDto;
import com.example.paymentservice.dto.PaymentKafkaDto;

public interface PaymentService {

    void pay(PaymentKafkaDto paymentKafkaDto);

    void resetPayment(ErrorKafkaDto errorKafkaDto);
}
