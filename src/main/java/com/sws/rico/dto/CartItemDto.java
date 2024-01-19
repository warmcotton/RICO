package com.sws.rico.dto;

import com.sws.rico.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter @Setter
public class CartItemDto {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @Min(1)
    private int count;
    private int price;
    @NotNull
    @Min(1)
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
