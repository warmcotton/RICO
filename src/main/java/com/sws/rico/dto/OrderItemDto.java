package com.sws.rico.dto;

import com.sws.rico.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {
    private Long id;
    private int count;
    private int price;
    private Long itemId;
    private Long deletedItemId;
    private String itemName;

    public static OrderItemDto getOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setCount(orderItem.getCount());
        orderItemDto.setPrice(orderItem.getPrice());

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
