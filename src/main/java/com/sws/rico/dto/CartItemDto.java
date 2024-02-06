package com.sws.rico.dto;

import com.sws.rico.entity.CartItem;
import com.sws.rico.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class CartItemDto {
    private Long id;
    private int count;
    private int price;
    private Long itemId;
    private String itemName;
    private List<ItemImgDto> itemImg;

    public static CartItemDto getInstance(CartItem cartItem, List<ItemImg> itemImgDtoList) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(cartItem.getCount());
        cartItemDto.setPrice(cartItem.getItem().getPrice());
        cartItemDto.setItemId(cartItem.getItem().getId());
        cartItemDto.setItemName(cartItem.getItem().getName());
        cartItemDto.setItemImg(itemImgDtoList.stream().map(ItemImgDto::getItemImgDto).collect(Collectors.toList()));
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
