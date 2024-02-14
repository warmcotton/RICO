package com.sws.rico.service;

import com.sws.rico.dto.OrderDto;
import com.sws.rico.dto.OrderItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommonOrderService {
    private final ItemImgRepository itemImgRepository;

    protected OrderDto getOrderDto(Order order, List<OrderItem> orderItemList) {
        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPrice(order.getPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setDate(order.getOrderDate());
        orderDto.setOrderItemDtoList(orderItemList.stream().map(orderItem ->
                OrderItemDto.getOrderItemDto(orderItem,  itemImgRepository.findAllByItem(orderItem.getItem()))
        ).collect(Collectors.toList()));
        orderDto.setUser(order.getUser().getEmail());
        return orderDto;
    }
}
