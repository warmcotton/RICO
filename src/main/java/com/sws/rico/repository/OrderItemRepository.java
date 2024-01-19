package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.Order;
import com.sws.rico.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteAllByItem(Item item);
    List<OrderItem> findByOrder(Order order);
    void deleteAllByOrder(Order order);
    List<OrderItem> findByItem(Item item);
}
