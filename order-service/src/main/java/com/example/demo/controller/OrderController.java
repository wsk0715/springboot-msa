package com.example.demo.controller;

import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.entity.Order;
import com.example.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    /**
     * 모든 주문 조회
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        log.info("GET /api/v1/orders - 모든 주문 조회 요청");
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * ID로 주문 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        log.info("GET /api/v1/orders/{} - 주문 조회 요청", id);
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 사용자 ID로 주문 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/orders/user/{} - 사용자 주문 목록 조회 요청", userId);
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 주문 생성
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest requestDto) {
        log.info("POST /api/v1/orders - 주문 생성 요청: 사용자 ID {}, 상품명 {}", requestDto.getUserId(), requestDto.getProductName());
        OrderResponse createdOrder = orderService.createOrder(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    /**
     * 주문 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest requestDto) {
        log.info("PUT /api/v1/orders/{} - 주문 수정 요청", id);
        OrderResponse updatedOrder = orderService.updateOrder(id, requestDto);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * 주문 상태 변경
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {
        log.info("PATCH /api/v1/orders/{}/status - 주문 상태 변경 요청: {}", id, status);
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * 주문 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        log.info("DELETE /api/v1/orders/{} - 주문 삭제 요청", id);
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 상태별 주문 수 조회
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<Long> getOrderCountByStatus(@PathVariable Order.OrderStatus status) {
        log.info("GET /api/v1/orders/count/status/{} - 상태별 주문 수 조회 요청", status);
        long count = orderService.getOrderCountByStatus(status);
        return ResponseEntity.ok(count);
    }

    /**
     * 사용자별 주문 수 조회
     */
    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Long> getOrderCountByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/orders/count/user/{} - 사용자별 주문 수 조회 요청", userId);
        long count = orderService.getOrderCountByUserId(userId);
        return ResponseEntity.ok(count);
    }

    /**
     * 사용자별 총 주문 금액 조회
     */
    @GetMapping("/total/user/{userId}")
    public ResponseEntity<Double> getTotalAmountByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/orders/total/user/{} - 사용자별 총 주문 금액 조회 요청", userId);
        Double totalAmount = orderService.getTotalAmountByUserId(userId);
        return ResponseEntity.ok(totalAmount);
    }
}
