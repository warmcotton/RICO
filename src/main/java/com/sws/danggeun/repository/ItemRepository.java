package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Item;
import com.sws.danggeun.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUser(User user);

    List<Item> findByNameContaining(String name);
}
