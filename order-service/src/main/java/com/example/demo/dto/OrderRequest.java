package com.example.demo.dto;

import com.example.demo.entity.Order;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    
    @NotNull(message = "사용자 ID는 필수입니다")
    private Long userId;
    
    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 1, max = 100, message = "상품명은 1-100자 사이여야 합니다")
    private String productName;
    
    @NotNull(message = "수량은 필수입니다")
    @DecimalMin(value = "1", message = "수량은 1 이상이어야 합니다")
    private Integer quantity;
    
    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.0", inclusive = false, message = "가격은 0보다 커야 합니다")
    private BigDecimal price;
    
    private Order.OrderStatus status;
}
