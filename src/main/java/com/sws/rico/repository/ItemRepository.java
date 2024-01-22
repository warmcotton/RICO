package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUser(User user);

    Page<Item> findByNameContaining(String name, Pageable pageable);
}
