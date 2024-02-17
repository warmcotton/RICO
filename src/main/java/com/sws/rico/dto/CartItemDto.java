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
}
