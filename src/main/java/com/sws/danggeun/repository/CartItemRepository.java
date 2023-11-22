package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Cart;
import com.sws.danggeun.entity.CartItem;
import com.sws.danggeun.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);

    List<CartItem> findByItem(Item item);
}
