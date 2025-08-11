package com.example.bookshop.dto.order;

import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.model.Status;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> orderItems;
    private LocalDateTime orderTime;
    private Status status;
    private String shippingAddress;
}
