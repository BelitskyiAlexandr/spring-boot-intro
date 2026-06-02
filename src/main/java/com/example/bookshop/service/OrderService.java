package com.example.bookshop.service;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderRequestDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.dto.status.StatusRequestDto;
import com.example.bookshop.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderDto> getAllOrdersByUser(User user, Pageable pageable);

    OrderDto updateStatus(Long orderId, StatusRequestDto statusRequestDto);

    OrderDto createOrder(User user, OrderRequestDto orderRequestDto);

    Page<OrderItemDto> getItemsByOrderId(Long orderId, User user, Pageable pageable);

    OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId);

    OrderDto getOrderById(Long orderId, User user);
}
