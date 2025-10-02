package com.example.demo.client;

import com.example.demo.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service", url = "http://localhost:8082")
public interface OrderServiceClient {
    
    /**
     * 사용자의 주문 목록 조회
     */
    @GetMapping("/api/v1/orders/user/{userId}")
    List<OrderResponse> getUserOrders(@PathVariable("userId") Long userId);
    
    /**
     * 주문 상세 조회
     */
    @GetMapping("/api/v1/orders/{orderId}")
    OrderResponse getOrderById(@PathVariable("orderId") Long orderId);
}
