package com.example.bookshop.service.impl;

import com.example.bookshop.dto.order.OrderDto;
import com.example.bookshop.dto.order.OrderRequestDto;
import com.example.bookshop.dto.orderitem.OrderItemDto;
import com.example.bookshop.dto.status.StatusRequestDto;
import com.example.bookshop.exception.EntityNotFoundException;
import com.example.bookshop.exception.OrderProccessingException;
import com.example.bookshop.mapper.OrderItemMapper;
import com.example.bookshop.mapper.OrderMapper;
import com.example.bookshop.model.Order;
import com.example.bookshop.model.OrderItem;
import com.example.bookshop.model.ShoppingCart;
import com.example.bookshop.model.Status;
import com.example.bookshop.model.User;
import com.example.bookshop.repository.order.OrderRepository;
import com.example.bookshop.repository.orderitem.OrderItemRepository;
import com.example.bookshop.repository.shoppingcart.ShoppingCartRepository;
import com.example.bookshop.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public Page<OrderDto> getAllOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findAllByUserId(user.getId(), pageable).map(orderMapper::toDto);
    }

    @Override
    public OrderDto updateStatus(Long orderId, StatusRequestDto statusRequestDto) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new EntityNotFoundException("Order not found with id: " + orderId));
        order.setStatus(statusRequestDto.getStatus());
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderDto createOrder(User user, OrderRequestDto orderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository.findByUserId(user.getId()).orElseThrow(()
                -> new EntityNotFoundException("ShoppingCart not found for user " + user.getId()));
        if (shoppingCart.getCartItems().isEmpty()) {
            throw new OrderProccessingException("Shopping cart is empty");
        }
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Status.PENDING);
        order.setShippingAddress(orderRequestDto.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        Set<OrderItem> orderItems = shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    return orderItem;
                })
                .collect(Collectors.toSet());
        order.setOrderItems(orderItems);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            BigDecimal itemTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }
        order.setTotal(total);

        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public Page<OrderItemDto> getItemsByOrderId(Long orderId, Pageable pageable) {
        return orderItemRepository.findByOrderId(orderId, pageable).map(orderItemMapper::toDto);
    }

    @Override
    public OrderItemDto getItemByOrderIdAndItemId(Long orderId, Long itemId) {
        return orderItemMapper.toDto(orderItemRepository.findByOrderIdAndItemId(orderId, itemId));
    }
}
