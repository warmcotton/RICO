package com.sws.danggeun.dto;

import com.sws.danggeun.entity.ItemStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDto {
    private String name;
    private int price;
    private int quantity;
    private ItemStatus itemStatus;
}
