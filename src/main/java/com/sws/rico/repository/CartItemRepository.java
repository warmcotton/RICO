package com.sws.rico.repository;

import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
import com.sws.rico.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByItem(Item item);
    void deleteAllByItem(Item item);
    void deleteAllByCart(Cart cart);
}
