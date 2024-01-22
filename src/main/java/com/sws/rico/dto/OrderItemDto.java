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
    private String itemName;

    public static OrderItemDto getOrderItemDto(OrderItem orderItem) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setCount(orderItem.getCount());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setItemId(orderItem.getItem().getId());
        orderItemDto.setItemName(orderItem.getItem().getName());
        return orderItemDto;
    }

    @Override
    public String toString() {
        return "OrderItemDto{" +
                "id=" + id +
                ", count=" + count +
                ", price=" + price +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}
