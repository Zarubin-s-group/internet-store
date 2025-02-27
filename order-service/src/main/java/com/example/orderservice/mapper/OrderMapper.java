package com.example.orderservice.mapper;

import com.example.orderservice.domain.Order;
import com.example.orderservice.dto.OrderListResponse;
import com.example.orderservice.dto.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(uses = OrderStatusMapper.class, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "createdAt", source = "order.creationTime")
    @Mapping(target = "modifiedAt", source = "order.modifiedTime")
    OrderResponse orderToResponse(Order order);

    List<OrderResponse> orderListToResponseList(List<Order> orders);

    default OrderListResponse orderListToOrderListResponse(Page<Order> orders) {
        List<OrderResponse> orderResponses =orderListToResponseList(orders.getContent());
        return new OrderListResponse(orders.getTotalElements(), orderResponses);
    }
}
