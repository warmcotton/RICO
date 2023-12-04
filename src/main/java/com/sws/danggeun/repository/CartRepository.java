package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Cart;
import com.sws.danggeun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
}
