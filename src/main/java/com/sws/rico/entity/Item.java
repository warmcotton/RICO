package com.sws.rico.entity;

import com.sws.rico.constant.ItemStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Item {
    @Id @Column(name = "item_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int quantity;
    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;
    @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Item getInstance(String name, int price, int quantity, ItemStatus itemStatus, User user) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setItemStatus(itemStatus);
        item.setUser(user);
        return item;
    }
}
