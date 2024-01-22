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
@Table(name = "delete_item")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DeletedItem {
    @Id
    @Column(name = "delete_item_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deletedAt;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public static DeletedItem getInstance(Item item) {
        DeletedItem deleted = new DeletedItem();
        deleted.setName(item.getName());
        deleted.setUser(item.getUser());
        return deleted;
    }
}
