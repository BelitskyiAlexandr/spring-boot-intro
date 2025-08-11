package com.example.bookshop.repository.orderItem;

import com.example.bookshop.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "book")
    Page<OrderItem> findByOrderId(Long orderId);

    @EntityGraph(attributePaths = "book")
    OrderItem findByOrderIdAndItemId(Long orderId, Long itemId);
}
