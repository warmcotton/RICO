package com.sws.danggeun.repository;

import com.sws.danggeun.constant.OrderStatus;
import com.sws.danggeun.entity.Order;
import com.sws.danggeun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);
}
