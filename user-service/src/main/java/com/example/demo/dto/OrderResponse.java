package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private Long userId;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
