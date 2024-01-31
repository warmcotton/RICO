package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByUser(User user);

    @Query(value = "select i from Item i " +
            "inner join fetch User u on i.user.id = u.id " +
            "where u.name like concat('%', :name, '%') or i.name like concat('%', :user, '%')")
    Page<Item> findPageItem(String name, String user, Pageable pageable);

    @Query(value = "select i from Item i " +
            "inner join User u on i.user.id = u.id " +
            "where lower(u.name) like lower(concat('%', :search, '%')) or lower(i.name) like lower(concat('%', :search, '%'))")
    Page<Item> findPageItemv2(String search, Pageable pageable);

    List<Item> findTop4ByOrderByCreatedAtDesc();
}
