package com.sws.rico.repository;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> findByIdWithPessimisticWriteLock(Long id);
    List<Item> findByUser(User user);

    @Query(value = "select i from Item i " +
            "inner join fetch User u on i.user.id = u.id " +
            "where u.email like concat('%', :email, '%') and i.name like concat('%', :name, '%')")
    Page<Item> findUserItem(String name, String email, Pageable pageable);

    @Query(value = "select i from Item i " +
            "inner join fetch User u on i.user.id = u.id " +
            "where lower(u.name) like lower(concat('%', :search, '%')) or lower(i.name) like lower(concat('%', :search, '%'))")
    Page<Item> findPageItemv2(String search, Pageable pageable);

    List<Item> findTop4ByOrderByCreatedAtDesc();
    @Query(value = "select i from Item i left join CategoryWrapper c on i.id = c.item.id where c.category = :category")
    Page<Item> findByCategory(CategoryDto category, Pageable pageable);

    List<Item> findTop8ByOrderByCreatedAtDesc();

    @Query(value = "select i from Item i left join Review r on i.id = r.item.id group by i.id order by avg(r.rating) desc")
    Page<Item> findPopularItem(Pageable pageable);
 }
