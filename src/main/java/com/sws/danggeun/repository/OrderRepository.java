package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
