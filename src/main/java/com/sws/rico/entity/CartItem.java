package com.sws.rico.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cart_item")
@Getter @Setter
public class CartItem {
    @Id @Column(name = "cart_item_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private int count;
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @JoinColumn(name = "cart_id") @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    public static CartItem createCartItem(int quantity, Item item, Cart newCart) {
        CartItem cartItem = new CartItem();
        cartItem.setCount(quantity);
        cartItem.setItem(item);
        cartItem.setCart(newCart);
        return cartItem;
    }
}
