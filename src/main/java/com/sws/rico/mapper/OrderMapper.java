package com.sws.rico.mapper;

import com.sws.rico.dto.OrderDto;
import com.sws.rico.dto.OrderItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
public class OrderMapper {
    private final ItemImgRepository itemImgRepository;
    private final OrderItemRepository orderItemRepository;

    private static OrderItemDto getOrderItemDto(OrderItem orderItem, List<ItemImg> itemImgDtoList) {
        OrderItemDto orderItemDto = new OrderItemDto();
        orderItemDto.setId(orderItem.getId());
        orderItemDto.setCount(orderItem.getCount());
        orderItemDto.setPrice(orderItem.getPrice());
        orderItemDto.setItemImg(itemImgDtoList.stream().map(ItemMapper::toItemImgDto).collect(Collectors.toList()));

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

    public OrderDto toOrderDto(Order order) {
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        OrderDto orderDto = new OrderDto();
        orderDto.setId(order.getId());
        orderDto.setPrice(order.getPrice());
        orderDto.setStatus(order.getStatus());
        orderDto.setDate(order.getOrderDate());
        orderDto.setOrderItemDtoList(orderItems.stream().map(orderItem ->
                getOrderItemDto(orderItem, itemImgRepository.findAllByItem(orderItem.getItem()))
        ).collect(Collectors.toList()));
        orderDto.setUser(order.getUser().getEmail());
        return orderDto;
    }
}
