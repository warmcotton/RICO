package com.sws.rico.service;

import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.OrderException;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    public Order order(List<Cart> cartList, String email) throws CustomException { //상품 수량 확인 -> ( 아이템 수량 차감 -> 주문 ) 하나의 트랜잭션
        User user = userRepository.findByEmail(email).get();
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
        int total = 0;
        for (Cart cart : cartList) {
            List<CartItem> cartItem = cartItemRepository.findByCart(cart);
            int order_item_price = 0;
            for (CartItem cit : cartItem) {
                Item item = cit.getItem();
                if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품 주문 x");
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


    public List<OrderItem> getOrderItems(Order order) {
        List<OrderItem> orderItemList = orderItemRepository.findByOrder(order);
        return orderItemList;
    }


    public boolean checkUser(Long id, String email) {
        return email.equals(getOrder(id).getUser().getEmail());
    }

    //12시간 후 배송완료
    @EventListener
    public void updateOrderStatus(ContextRefreshedEvent event) {
        List<Order> orderList = orderRepository.findByStatus(OrderStatus.ORDER);
        for(Order order : orderList) {
            long second = Duration.between(order.getOrderDate(), LocalDateTime.now()).getSeconds();
            if (second > 60 * 60 * 12) order.setStatus(OrderStatus.COMPLETE);
        }
    }


    public Page<OrderDto> getOrderDtoList(String email, Pageable page) {
        User user = userRepository.findByEmail(email).get();
        return orderRepository.findByUser(user, page)
                .map(order -> OrderDto.getOrderDto(order, getOrderItems(order)));
    }
}
