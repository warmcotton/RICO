package com.sws.rico.dto;

import com.sws.rico.entity.ItemImg;
import com.sws.rico.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class OrderItemDto {
    private Long id;
    private int count;
    private int price;
    private Long itemId;
    private Long deletedItemId;
    private String itemName;
    private List<ItemImgDto> itemImg;
}
