package com.example.orderservice.mapper;

import com.example.orderservice.domain.OrderStatusHistory;
import com.example.orderservice.dto.StatusDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderStatusMapper {

    OrderStatusMapper INSTANCE = Mappers.getMapper(OrderStatusMapper.class);

    StatusDto statusToDto(OrderStatusHistory orderStatusHistory);

    List<StatusDto> statusListToDtoList(List<OrderStatusHistory> orderStatuses);
}
