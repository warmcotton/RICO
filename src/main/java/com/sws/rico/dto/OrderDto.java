package com.sws.rico.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sws.rico.entity.Order;
import com.sws.rico.entity.OrderItem;
import com.sws.rico.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class OrderDto {
    private Long id;
    private int price;
    private OrderStatus status;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private List<OrderItemDto> orderItemDtoList;
    private String user;
}
