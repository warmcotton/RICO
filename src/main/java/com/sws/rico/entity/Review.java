package com.sws.rico.entity;

import com.sws.rico.dto.ReviewDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Review {
    @Id
    @Column(name = "review_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private double rating;
    @Column(nullable = false)
    private String review;

    public static Review getInstance(Item item, User user, double rating, String user_review) {
        Review review = new Review();
        review.setItem(item);
        review.setUser(user);
        review.setRating(rating);
        review.setReview(user_review);
        return review;
    }
}
