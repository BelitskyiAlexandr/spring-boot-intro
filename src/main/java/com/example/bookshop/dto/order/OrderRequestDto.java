package com.example.bookshop.dto.order;

import com.example.bookshop.dto.orderitem.OrderItemDto;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderRequestDto {
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private String shippingAddress;
}
