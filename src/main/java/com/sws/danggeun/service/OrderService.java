package com.sws.danggeun.service;

import com.sws.danggeun.constant.OrderStatus;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    //조회
    public Order getOrder(Long id) {
        return orderRepository.findById(id).get();
    }
    //주문
    public Order order(List<Cart> cartList, String email) { //상품 수량 확인 -> ( 아이템 수량 차감 -> 주문 ) 하나의 트랜잭션
        User user = userRepository.findByEmail(email).get();
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
        int total = 0;
        for (Cart cart : cartList) {
            List<CartItem> cartItem = cartItemRepository.findByCart(cart);
            int order_item_price = 0;
            for (CartItem cit : cartItem) {
                Item item = cit.getItem();
                int price = item.getPrice()*cit.getCount();
                order_item_price += price;
                OrderItem newOrderItem = OrderItem.getInstance(item,newOrder,cit.getCount(),price);
                orderItemRepository.save(newOrderItem);
                cartItemRepository.delete(cit);
            }
            total += order_item_price;
            cartRepository.delete(cart);
        }
        newOrder.setPrice(total);
        return newOrder;
    }
    //주문취소 : 수량업데이트
    public void deleteOrder(Long id) throws Exception {
        Order order = getOrder(id);
        if(order.getStatus()==OrderStatus.ORDER) throw new Exception("주문 취소 먼저");
        orderItemRepository.deleteAllByOrder(order);
        orderRepository.deleteById(id);
    }

    public List<Order> getOrders(String email) {
        User user = userRepository.findByEmail(email).get();
        return orderRepository.findByUser(user);
    }

    public List<OrderItem> getOrderItems(Order order) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrder(order);
        return orderItemList;
    }
}
