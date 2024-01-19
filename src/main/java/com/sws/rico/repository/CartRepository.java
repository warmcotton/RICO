package com.sws.rico.repository;

import com.sws.rico.entity.Cart;
import com.sws.rico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);
}
