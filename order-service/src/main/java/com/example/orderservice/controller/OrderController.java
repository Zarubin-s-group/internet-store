package com.example.orderservice.controller;

import com.example.orderservice.domain.Order;
import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.OrderListResponse;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.exception.AccessDeniedException;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.utils.RequestHeaderUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders in delivery system", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/order")
    public ResponseEntity<OrderListResponse> getAllOrders(HttpServletRequest request, Pageable pageable) {
        return ResponseEntity.ok(OrderMapper.INSTANCE.orderListToOrderListResponse(orderService.getAllOrders(RequestHeaderUtils.getActiveUserId(request),
                        RequestHeaderUtils.isAdmin(request), pageable)));
    }

    @Operation(summary = "Get an order by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(HttpServletRequest request,
                                                 @PathVariable @Parameter(description = "Id of order") Long orderId) {
        Order order = orderService.getOrder(orderId);
        if(!order.getUserId().equals(RequestHeaderUtils.getActiveUserId(request)) &&
                !RequestHeaderUtils.isAdmin(request)) {
            throw new AccessDeniedException("Only the customer who placed the order can receive information about this order");
        } else {
            return ResponseEntity.ok(OrderMapper.INSTANCE.orderToResponse(order));
        }
    }

    @Operation(summary = "Add order and start delivery process for it", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/order")
    public ResponseEntity<?> addOrder(HttpServletRequest request, @Valid @RequestBody CreateOrderRequest createOrderRequest) {
        return orderService.addOrder(RequestHeaderUtils.getActiveUserId(request), createOrderRequest)
                .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.INSTANCE.orderToResponse(order)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build());
    }
}
