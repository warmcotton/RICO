package com.sws.rico.repository;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CategoryRepository extends JpaRepository<CategoryWrapper, Long> {
    void deleteAllByItem(Item item);

    Set<CategoryWrapper> findAllByItem(Item item);
}
