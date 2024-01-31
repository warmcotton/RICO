package com.sws.rico.dto;

import com.sws.rico.entity.Item;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ItemDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    @Min(1000)
    private int price;
    @NotNull
    @Min(1)
    private int quantity;
    @NotNull
    private ItemStatus itemStatus;
    private List<ItemImgDto> itemImgDtoList;
    private String user; //

    public static ItemDto getItemDto(Item i, List<ItemImg> itemImgDtoList) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(i.getId());
        itemDto.setName(i.getName());
        itemDto.setPrice(i.getPrice());
        itemDto.setQuantity(i.getQuantity());
        itemDto.setItemStatus(i.getItemStatus());
        itemDto.setItemImgDtoList(itemImgDtoList.stream().map(ItemImgDto::getItemImgDto).collect(Collectors.toList()));
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
                ", itemImgDtoList=" + itemImgDtoList +
                ", user='" + user + '\'' +
                '}';
    }
}
