package com.sws.danggeun.dto;

import com.sws.danggeun.entity.Order;
import com.sws.danggeun.entity.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class OrderDto {
    private Long id;
    private int price;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private String user;

    public static OrderDto getInstance(Order order) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPrice(order.getPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setOrderDate(order.getOrderDate());
        orderDto.setUser(order.getUser().getName());
        return orderDto;
    }
    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", price=" + price +
                ", status=" + status +
                ", orderDate=" + orderDate +
                ", user='" + user + '\'' +
                '}';
    }
}
