package com.sws.rico.repository;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Set;

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
    @Query(value = "select i from Item i left join CategoryWrapper c on i.id = c.item.id where c.category = :category")
    Page<Item> findByCategory(CategoryDto category, Pageable pageable);
}
