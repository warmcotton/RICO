package com.sws.rico.repository;

import com.sws.rico.entity.Cart;
import com.sws.rico.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Page<Cart> findByUser(User user, Pageable pageable);
}
