package com.sws.rico.dto;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @NotEmpty
    @Size(min = 5, max = 50)
    private String brief;
    @NotEmpty
    @Size(min = 10, max = 2000)
    private String description;

    private Set<CategoryDto> category;
    private List<ItemImgDto> itemImgDtoList;
    private String user; //

    public static ItemDto getItemDto(Item i, Set<CategoryWrapper> category, List<ItemImg> itemImgDtoList) {
        ItemDto itemDto = new ItemDto();

        Set<CategoryDto> categoryDtos = new HashSet<>();
        for(CategoryWrapper categoryWrapper : category) {
            categoryDtos.add(categoryWrapper.getCategory());
        }

        itemDto.setId(i.getId());
        itemDto.setName(i.getName());
        itemDto.setPrice(i.getPrice());
        itemDto.setQuantity(i.getQuantity());
        itemDto.setItemStatus(i.getItemStatus());
        itemDto.setBrief(i.getBrief());
        itemDto.setDescription(i.getDescription());
        itemDto.setCategory(categoryDtos);
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
                ", brief='" + brief + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                ", itemImgDtoList=" + itemImgDtoList +
                ", user='" + user + '\'' +
                '}';
    }
}
