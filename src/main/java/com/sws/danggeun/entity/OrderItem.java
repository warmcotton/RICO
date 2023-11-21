package com.sws.danggeun.entity;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
public class OrderItem {
    @Id @Column(name = "order_item_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int count;
    @Column(nullable = false)
    private int price;
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @JoinColumn(name = "order_id") @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
}
