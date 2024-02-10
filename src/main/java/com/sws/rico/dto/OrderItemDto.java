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

    public static OrderItemDto getOrderItemDto(OrderItem orderItem, List<ItemImg> itemImgDtoList) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setCount(orderItem.getCount());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setItemImg(itemImgDtoList.stream().map(ItemImgDto::getItemImgDto).collect(Collectors.toList()));

        if(orderItem.getItem() != null) {
            orderItemDto.setItemId(orderItem.getItem().getId());
            orderItemDto.setItemName(orderItem.getItem().getName());
        }
        else {
            orderItemDto.setDeletedItemId(orderItem.getDeletedItem().getId());
            orderItemDto.setItemName(orderItem.getDeletedItem().getName());
        }
        return orderItemDto;
    }

    @Override
    public String toString() {
        return "OrderItemDto{" +
                "id=" + id +
                ", count=" + count +
                ", price=" + price +
                ", itemId=" + itemId +
                ", deletedItemId=" + deletedItemId +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
