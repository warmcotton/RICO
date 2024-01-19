package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUser(User user);

    List<Item> findByNameContaining(String name);
}