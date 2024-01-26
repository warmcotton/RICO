package com.sws.rico.repository;

import com.sws.rico.constant.OrderStatus;
import com.sws.rico.entity.Order;
import com.sws.rico.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUser(User user, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);
}
