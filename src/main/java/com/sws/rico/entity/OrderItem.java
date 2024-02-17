package com.sws.rico.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
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
    @JoinColumn(name = "delete_item_id") @ManyToOne(fetch = FetchType.LAZY)
    private DeletedItem deletedItem;

    public static OrderItem createOrderItem(Item item, Order order, int count, int price) {
        OrderItem newOrderItem = new OrderItem();
        newOrderItem.setItem(item);
        newOrderItem.setOrder(order);
        newOrderItem.setCount(count);
        newOrderItem.setPrice(price);
        return newOrderItem;
    }
}
