package com.example.bookshop.controller;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderRequestDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.dto.status.StatusRequestDto;
import com.example.bookshop.model.User;
import com.example.bookshop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderDto createOrder(@AuthenticationPrincipal User user,
                                @RequestBody @Valid OrderRequestDto orderRequestDto) {
        return orderService.createOrder(user, orderRequestDto);
    }

    @GetMapping
    public Page<OrderDto> getAll(@AuthenticationPrincipal User user, Pageable pageable) {
        return orderService.getAllOrdersByUser(user, pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public OrderDto updateOrderStatus(@PathVariable Long id,
                                      @RequestBody @Valid StatusRequestDto statusRequestDto) {
        return orderService.updateStatus(id, statusRequestDto);
    }

    @GetMapping("/{orderId}/items")
    public Page<OrderItemDto> getItemsByOrder(@PathVariable Long orderId, Pageable pageable) {
        return orderService.getItemsByOrderId(orderId, pageable);
    }

    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItemById(@PathVariable Long orderId, @PathVariable Long itemId) {
        return  orderService.getItemByOrderIdAndItemId(orderId, itemId);
    }
}
