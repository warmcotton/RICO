package com.sws.danggeun.service;

import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.Cart;
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
    public OrderDto buySingleItem(Long id, int quantity, String email) throws Exception {
        if (!itemService.checkAndReduce(id, quantity)) throw new Exception("수량 없음");

        List<Cart> cart = new ArrayList<>();
        Cart ct = cartService.createCartWithSingleItem(id, quantity, email); //createCart x
        cart.add(ct);

        OrderDto orderDto = orderService.order(cart, email);

        return orderDto;
    }

}
