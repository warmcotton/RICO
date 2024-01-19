package com.sws.rico.service;

import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.*;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.OrderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConsumerService {
    private final CartService cartService;
    private final OrderService orderService;
    private final ItemService itemService;
    //단일 아이템 주문 : 수량확인 -> 카트생성 -> 주문
    public OrderDto orderSingleItem(Long id, int quantity, String email) throws CustomException {
        if (!itemService.checkAndReduce(id, quantity)) throw new ItemException("수량 없음");
        List<Cart> cart = new ArrayList<>();
        Cart ct = cartService.createCartWithSingleItem(id, quantity, email); //createCart x
        cart.add(ct);
        Order order = orderService.order(cart, email);
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        return OrderDto.getInstance(order, orderItemList);
    }
    //복수 카트 주문
    public OrderDto orderCarts(List<CartDto> cartDtoList, String email) throws CustomException{
        List<Cart> cartList = new ArrayList<>();
        for (CartDto cartDto : cartDtoList) {
            for(CartItemDto cartItemDto : cartDto.getCartItemDto()) {
                if (!itemService.checkAndReduce(cartItemDto.getItemId(),cartItemDto.getCount())) throw new ItemException("수량 없음");
            }
            Cart cart = cartService.getCart(cartDto.getId());
            cartList.add(cart);
        }
        Order order = orderService.order(cartList, email);
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        return OrderDto.getInstance(order, orderItemList);
    }

    public List<ItemDto> getItemDtoList() {
        List<Item> itemList = itemService.getItems();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for(Item i : itemList) {
            List<ItemImg> itemImgList = itemService.getItemImgs(i);
            ItemDto newItemDto = ItemDto.getDto(i, itemImgList);
            itemDtoList.add(newItemDto);
        }
        return itemDtoList;
    }

    public List<OrderDto> getOrderDtoList(String email) {
        List<Order> orderList = orderService.getOrders(email);
        List<OrderDto> orderDtoList = new ArrayList<>();
        for(Order o : orderList) {
            List<OrderItem> orderItemList = orderService.getOrderItems(o);
            OrderDto orderDto = OrderDto.getInstance(o, orderItemList);
            orderDtoList.add(orderDto);
        }
        return orderDtoList;
    }
    //주문취소
    public void cancel(Long id, String email) throws CustomException {
        if(!orderService.checkUser(id, email)) throw new OrderException("접근 권한 없음");
        Order order = orderService.getOrder(id);
        if(order.getStatus()== OrderStatus.CANCEL) throw new OrderException("이미 취소됨");
        else if (order.getStatus() == OrderStatus.COMPLETE) throw new OrderException("배송 완료됨");
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        for(OrderItem orderItem : orderItemList) {
            itemService.restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }
}