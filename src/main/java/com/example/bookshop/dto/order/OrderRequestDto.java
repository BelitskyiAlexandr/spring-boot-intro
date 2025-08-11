package com.example.bookshop.dto.order;

import com.example.bookshop.dto.orderitem.OrderItemDto;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
public class OrderRequestDto {
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private String shippingAddress;
}
