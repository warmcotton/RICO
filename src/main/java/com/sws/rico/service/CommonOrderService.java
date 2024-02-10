package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.dto.OrderItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.OrderException;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = {CustomException.class})
public class CommonOrderService {
    private final ItemImgRepository itemImgRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

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

    protected List<OrderItem> getOrderItems(Order order) {
        return orderItemRepository.findByOrder(order);
    }

    protected Item checkItemStatus(Long itemId, int count, String email) throws CustomException {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("상품 판매중이 아닙니다.");
        if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품은 주문할 수 없습니다.");
        boolean ct = count <= itemRepository.findById(itemId).get().getQuantity();
        if (!ct) throw new ItemException("수량 없음");
        return item;
    }

    protected Item reduceItemQuantity(Item item, int count) {
        item.setQuantity(item.getQuantity() - count);
        if(item.getQuantity() - count == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        return item;
    }

    protected Order saveOrder(int count, String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
//        orderItemRepository.save(OrderItem.getInstance(item,newOrder, count, item.getPrice()));
//        newOrder.setPrice(item.getPrice() * count);
        return newOrder;
    }

    protected Order saveOrderItem(Order order, Item item, int count) {
        orderItemRepository.save(OrderItem.getInstance(item, order, count, item.getPrice()));
        order.setPrice(item.getPrice() * count);
        return order;
    }
}
