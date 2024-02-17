package com.sws.rico.entity;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.dto.ItemDto;
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
    @Column(nullable = false)
    private String brief;
    @Column(nullable = false)
    private String description;
    @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static Item createItem(ItemDto itemDto, User user) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setItemStatus(itemDto.getItemStatus());
        item.setBrief(itemDto.getBrief());
        item.setDescription(itemDto.getDescription());
        item.setUser(user);
        return item;
    }
}
