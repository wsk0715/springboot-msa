package com.example.demo.service;

import com.example.demo.client.UserServiceClient;
import com.example.demo.dto.OrderRequest;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Order;
import com.example.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final UserServiceClient userServiceClient;
    
    /**
     * 모든 주문 조회
     */
    public List<OrderResponse> getAllOrders() {
        log.info("모든 주문 조회 요청");
        return orderRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ID로 주문 조회
     */
    public OrderResponse getOrderById(Long id) {
        log.info("주문 조회 요청 - ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + id));
        return convertToResponseDto(order);
    }
    
    /**
     * 사용자 ID로 주문 목록 조회
     */
    public List<OrderResponse> getOrdersByUserId(Long userId) {
        log.info("사용자 주문 목록 조회 요청 - 사용자 ID: {}", userId);
        
        // 사용자 존재 여부 확인
        try {
            UserResponse user = userServiceClient.getUserById(userId);
            log.info("사용자 확인 완료 - 사용자 ID: {}, 이름: {}", userId, user.getName());
        } catch (Exception e) {
            log.error("사용자 서비스 호출 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }
        
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 주문 생성
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest requestDto) {
        log.info("주문 생성 요청 - 사용자 ID: {}, 상품명: {}", requestDto.getUserId(), requestDto.getProductName());
        
        // 사용자 존재 여부 확인
        try {
            UserResponse user = userServiceClient.getUserById(requestDto.getUserId());
            log.info("사용자 확인 완료 - 사용자 ID: {}, 이름: {}", requestDto.getUserId(), user.getName());
        } catch (Exception e) {
            log.error("사용자 서비스 호출 실패 - 사용자 ID: {}, 오류: {}", requestDto.getUserId(), e.getMessage());
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + requestDto.getUserId());
        }
        
        Order order = Order.builder()
                .userId(requestDto.getUserId())
                .productName(requestDto.getProductName())
                .quantity(requestDto.getQuantity())
                .price(requestDto.getPrice())
                .status(requestDto.getStatus() != null ? requestDto.getStatus() : Order.OrderStatus.PENDING)
                .build();
        
        Order savedOrder = orderRepository.save(order);
        log.info("주문 생성 완료 - ID: {}", savedOrder.getId());
        
        return convertToResponseDto(savedOrder);
    }
    
    /**
     * 주문 정보 수정
     */
    @Transactional
    public OrderResponse updateOrder(Long id, OrderRequest requestDto) {
        log.info("주문 수정 요청 - ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + id));
        
        // 사용자 존재 여부 확인 (사용자 ID가 변경된 경우)
        if (!order.getUserId().equals(requestDto.getUserId())) {
            try {
                UserResponse user = userServiceClient.getUserById(requestDto.getUserId());
                log.info("사용자 확인 완료 - 사용자 ID: {}, 이름: {}", requestDto.getUserId(), user.getName());
            } catch (Exception e) {
                log.error("사용자 서비스 호출 실패 - 사용자 ID: {}, 오류: {}", requestDto.getUserId(), e.getMessage());
                throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + requestDto.getUserId());
            }
        }
        
        order.setUserId(requestDto.getUserId());
        order.setProductName(requestDto.getProductName());
        order.setQuantity(requestDto.getQuantity());
        order.setPrice(requestDto.getPrice());
        if (requestDto.getStatus() != null) {
            order.setStatus(requestDto.getStatus());
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.info("주문 수정 완료 - ID: {}", updatedOrder.getId());
        
        return convertToResponseDto(updatedOrder);
    }
    
    /**
     * 주문 상태 변경
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        log.info("주문 상태 변경 요청 - ID: {}, 상태: {}", id, status);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + id));
        
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        log.info("주문 상태 변경 완료 - ID: {}, 상태: {}", updatedOrder.getId(), updatedOrder.getStatus());
        
        return convertToResponseDto(updatedOrder);
    }
    
    /**
     * 주문 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteOrder(Long id) {
        log.info("주문 삭제 요청 - ID: {}", id);
        
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다. ID: " + id));
        
        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        
        log.info("주문 삭제 완료 - ID: {}", id);
    }
    
    /**
     * 상태별 주문 수 조회
     */
    public long getOrderCountByStatus(Order.OrderStatus status) {
        log.info("상태별 주문 수 조회 요청 - 상태: {}", status);
        return orderRepository.countByStatus(status);
    }
    
    /**
     * 사용자별 주문 수 조회
     */
    public long getOrderCountByUserId(Long userId) {
        log.info("사용자별 주문 수 조회 요청 - 사용자 ID: {}", userId);
        return orderRepository.countByUserId(userId);
    }
    
    /**
     * 사용자별 총 주문 금액 조회
     */
    public Double getTotalAmountByUserId(Long userId) {
        log.info("사용자별 총 주문 금액 조회 요청 - 사용자 ID: {}", userId);
        Double totalAmount = orderRepository.getTotalAmountByUserId(userId);
        return totalAmount != null ? totalAmount : 0.0;
    }
    
    /**
     * Entity를 ResponseDto로 변환
     */
    private OrderResponse convertToResponseDto(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .productName(order.getProductName())
                .quantity(order.getQuantity())
                .price(order.getPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
