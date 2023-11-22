package com.sws.danggeun.service;

import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.CartItemRepository;
import com.sws.danggeun.repository.OrderItemRepository;
import com.sws.danggeun.repository.OrderRepository;
import com.sws.danggeun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    public OrderDto order(List<Cart> cartList, String email) { //상품 수량 확인 -> ( 아이템 수량 차감 -> 주문 ) 하나의 트랜잭션
        User user = userRepository.findByEmail(email).get();
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));

        int total = 0;

        for (Cart cart : cartList) {
            List<CartItem> cartItem = cartItemRepository.findByCart(cart);
            int order_item_price = 0;
            for (CartItem cit : cartItem) {
                List<CartItem> items = cartItemRepository.findByItem(cit.getItem());
                for(CartItem citem : items) {
                    int price = citem.getItem().getPrice()*citem.getCount();
                    order_item_price += price;
                    OrderItem newOrderItem = OrderItem.getInstance(citem.getItem(),newOrder,citem.getCount(), price);
                    orderItemRepository.save(newOrderItem);
                }
            }
            total += order_item_price;
        }

        newOrder.setPrice(total);
        OrderDto order = OrderDto.newInstance(newOrder);
        return order;
    }
}
