package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // 사용자 ID로 주문 목록 조회
    List<Order> findByUserId(Long userId);
    
    // 사용자 ID와 상태로 주문 목록 조회
    List<Order> findByUserIdAndStatus(Long userId, Order.OrderStatus status);
    
    // 상태별 주문 수 조회
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(Order.OrderStatus status);
    
    // 사용자별 주문 수 조회
    @Query("SELECT COUNT(o) FROM Order o WHERE o.userId = :userId")
    long countByUserId(Long userId);
    
    // 사용자별 총 주문 금액 조회
    @Query("SELECT SUM(o.price * o.quantity) FROM Order o WHERE o.userId = :userId AND o.status != 'CANCELLED'")
    Double getTotalAmountByUserId(Long userId);
}
