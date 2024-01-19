package com.sws.rico.repository;

import com.sws.rico.constant.OrderStatus;
import com.sws.rico.entity.Order;
import com.sws.rico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);
}
