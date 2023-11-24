package com.sws.danggeun.dto;

import com.sws.danggeun.entity.Item;
import com.sws.danggeun.entity.ItemStatus;
import com.sws.danggeun.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemDto {
    private Long id;
    private String name;
    private int price;
    private int quantity;
    private ItemStatus itemStatus;
    private String user; //

    public static ItemDto getDto(Item i) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(i.getId());
        itemDto.setName(i.getName());
        itemDto.setPrice(i.getPrice());
        itemDto.setQuantity(i.getQuantity());
        itemDto.setItemStatus(i.getItemStatus());
        itemDto.setUser(i.getUser().getName());

        return itemDto;
    }

    @Override
    public String toString() {
        return "ItemDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", itemStatus=" + itemStatus +
                ", user='" + user + '\'' +
                '}';
    }
}
