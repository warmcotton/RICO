package com.sws.danggeun.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Item {
    @Id @Column(name = "item_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int quantity;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @JoinColumn(name = "cart_id") @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;
    @JoinColumn(name = "image_id") @ManyToOne(fetch = FetchType.LAZY)
    private Image image;
    @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
