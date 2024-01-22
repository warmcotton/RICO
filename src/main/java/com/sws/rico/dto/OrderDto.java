package com.sws.rico.dto;

import com.sws.rico.entity.Order;
import com.sws.rico.entity.OrderItem;
import com.sws.rico.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class OrderDto {
    private Long id;
    private int price;
    private OrderStatus status;
    private LocalDateTime date;
    private List<OrderItemDto> orderItemDtoList;
    private String user;

    public static OrderDto getOrderDto(Order order, List<OrderItem> orderItemList) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPrice(order.getPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setDate(order.getOrderDate());
        orderDto.setOrderItemDtoList(orderItemList.stream().map(OrderItemDto::getOrderItemDto).collect(Collectors.toList()));
        orderDto.setUser(order.getUser().getEmail());
        return orderDto;
    }

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", price=" + price +
                ", status=" + status +
                ", date=" + date +
                ", orderItemDtoList=" + orderItemDtoList +
                ", user='" + user + '\'' +
                '}';
    }
}
