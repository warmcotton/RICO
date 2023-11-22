package com.sws.danggeun.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "order")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
public class Order {
    @Id @Column(name = "order_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private int price;
    @JoinColumn(name = "user_id") @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @CreatedDate @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;


    public static Order getInstance(User user, OrderStatus status) {
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setStatus(status);

        return newOrder;
    }
}
