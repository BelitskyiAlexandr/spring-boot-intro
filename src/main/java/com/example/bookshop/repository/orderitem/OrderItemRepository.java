package com.example.bookshop.repository.orderitem;

import com.example.bookshop.model.OrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @EntityGraph(attributePaths = "book")
    Page<OrderItem> findByOrderId(Long orderId, Pageable pageable);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.id = :itemId")
    OrderItem findByOrderIdAndItemId(@Param("orderId") Long orderId, @Param("itemId") Long itemId);
}
