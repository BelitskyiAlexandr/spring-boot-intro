package com.example.bookshop.dto.orderitem;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderItemDto {
    private Long id;
    private Long bookId;
    private int quantity;
}
