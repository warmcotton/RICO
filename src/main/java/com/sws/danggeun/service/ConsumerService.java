package com.sws.danggeun.service;

import com.sws.danggeun.constant.OrderStatus;
import com.sws.danggeun.dto.CartDto;
import com.sws.danggeun.dto.CartItemDto;
import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.token.TokenInfo;
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
    private final UserService userService;
    //단일 아이템 주문 : 수량확인 -> 카트생성 -> 주문
    public OrderDto buySingleItem(Long id, int quantity, String email) throws Exception {
        if (!itemService.checkAndReduce(id, quantity)) throw new Exception("수량 없음");
        List<Cart> cart = new ArrayList<>();
        Cart ct = cartService.createCartWithSingleItem(id, quantity, email); //createCart x
        cart.add(ct);
        Order order = orderService.order(cart, email);
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        return OrderDto.getInstance(order, orderItemList);
    }
    //복수 카트 주문
    public OrderDto buyCarts(List<CartDto> cartDtoList, String email) throws Exception{
        List<Cart> cartList = new ArrayList<>();
        for (CartDto cartDto : cartDtoList) {
            for(CartItemDto cartItemDto : cartDto.getCartItemDto()) {
                if (!itemService.checkAndReduce(cartItemDto.getItemId(),cartItemDto.getCount())) throw new Exception("수량 없음");
            }
            Cart cart = cartService.getCart(cartDto.getId());
            cartList.add(cart);
        }
        Order order = orderService.order(cartList, email);
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        return OrderDto.getInstance(order, orderItemList);
    }

    public List<CartDto> viewCartList(String email) {
        List<Cart> cartList = cartService.getCarts(email);
        List<CartDto> cartDtoList = new ArrayList<>();
        for(Cart c : cartList) {
            List<CartItem> cartItemList = cartService.getCartItems(c);
            CartDto newCartDto = CartDto.getInstance(c, cartItemList);
            cartDtoList.add(newCartDto);
        }
        return cartDtoList;
    }

    public List<ItemDto> viewItemList() {
        List<Item> itemList = itemService.getItems();
        List<ItemDto> itemDtoList = new ArrayList<>();
        for(Item i : itemList) {
            ItemDto newItemDto = ItemDto.getDto(i);
            itemDtoList.add(newItemDto);
        }
        return itemDtoList;
    }

    public List<OrderDto> viewOrderList(String email) {
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
    public void cancel(Long id) throws Exception {
        Order order = orderService.getOrder(id);
        if(order.getStatus()== OrderStatus.CANCEL) throw new Exception("이미 취소됨");
        List<OrderItem> orderItemList = orderService.getOrderItems(order);
        for(OrderItem orderItem : orderItemList) {
            itemService.restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }
}