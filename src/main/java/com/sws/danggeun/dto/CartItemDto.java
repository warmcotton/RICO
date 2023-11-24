package com.sws.danggeun.dto;

import com.sws.danggeun.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CartItemDto {
    private Long id;
    private int count;
    private int price;
    private Long itemId;
    private String itemName;

    public static CartItemDto getInstance(CartItem cartItem) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setId(cartItem.getId());
        cartItemDto.setCount(cartItem.getCount());
        cartItemDto.setPrice(cartItem.getItem().getPrice());
        cartItemDto.setItemId(cartItem.getItem().getId());
        cartItemDto.setItemName(cartItem.getItem().getName());
        return cartItemDto;
    }

    @Override
    public String toString() {
        return "CartItemDto{" +
                "id=" + id +
                ", count=" + count +
                ", price=" + price +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
