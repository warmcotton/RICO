package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
