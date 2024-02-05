package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.Review;
import com.sws.rico.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository  extends JpaRepository<Review, Long> {
    List<Review> findAllByItem(Item item);

    Optional<Review> findByItemAndUser(Item item, User user);
}
