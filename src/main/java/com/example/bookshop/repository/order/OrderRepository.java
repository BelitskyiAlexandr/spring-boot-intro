package com.example.bookshop.repository.order;

import com.example.bookshop.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = "orderItems")
    Optional<Order> findWithItemsById(Long orderId);

    @EntityGraph(attributePaths = "orderItems")
    Page<Order> findAllByUserId(Long userId, Pageable pageable);
}
