package com.sws.rico.service;

import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.*;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(rollbackFor = {CustomException.class})
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommonOrderService commonOrderService;
    private final CommonUserService commonUserService;
    private final CommonItemService commonItemService;

    public OrderDto getOrderDto(Long orderId, String email) throws CustomException {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if (!order.getUser().getEmail().equals(email)) throw new OrderException("접근 권한 없습니다.");
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
        return commonOrderService.getOrderDto(order, orderItems);
    }

    public List<OrderDto> getOrderDtoPage(String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        return orderRepository.findTop50ByUserOrderByOrderDateDesc(user)
                .stream().map(order -> commonOrderService.getOrderDto(order, commonOrderService.getOrderItems(order))).collect(toList());
    }

    public OrderDto orderItem(Long itemId, int count, String email) throws CustomException {
        Item item = commonOrderService.checkItemStatus(itemId, count, email);
        item = commonOrderService.reduceItemQuantity(item, count);
        Order newOrder = commonOrderService.saveOrder(count, email);

        newOrder = commonOrderService.saveOrderItem(newOrder, item, count);

        List<OrderItem> orderItemList = commonOrderService.getOrderItems(newOrder);
        return commonOrderService.getOrderDto(newOrder, orderItemList);
    }

    public OrderDto orderCartv2(String email) throws CustomException {
        int total = 0;
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니가 생성되지 않았습니다."));
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품을 주문해야 합니다.");

        Order newOrder = commonOrderService.saveOrder(total, email);
        for (CartItem cit : cartItemRepository.findByCart(cart)) {
            Item item = cit.getItem();
            item = commonOrderService.checkItemStatus(item.getId(), cit.getCount(), email);
            item = commonOrderService.reduceItemQuantity(item,  cit.getCount());

            newOrder = commonOrderService.saveOrderItem(newOrder, item, cit.getCount());

            total += item.getPrice()*cit.getCount();
            cartItemRepository.delete(cit);
        }
        cartRepository.delete(cart);
        newOrder.setPrice(total);
        List<OrderItem> orderItemList = commonOrderService.getOrderItems(newOrder);
        return commonOrderService.getOrderDto(newOrder, orderItemList);
    }

    public void cancel(Long orderId, String email) throws CustomException {
        if(!commonUserService.validateUserOrder(orderId, email)) throw new OrderException("접근 권한이 없습니다.");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if(order.getStatus()== OrderStatus.CANCEL) throw new OrderException("주문이 이미 취소되었습니다.");
        else if (order.getStatus() == OrderStatus.COMPLETE) throw new OrderException("배송이 이미 완료되었습니다.");
        List<OrderItem> orderItemList = commonOrderService.getOrderItems(order);
        for(OrderItem orderItem : orderItemList) {
            commonItemService.restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }

    public void deleteOrder(Long orderId, String email) throws CustomException {
        if(!commonUserService.validateUserOrder(orderId, email)) throw new OrderException("접근 권한이 없습니다");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if(order.getStatus()==OrderStatus.ORDER) throw new OrderException("주문 취소가 되지 않았습니다.");
        orderItemRepository.deleteAllByOrder(order);
        orderRepository.deleteById(orderId);
    }

    @EventListener
    public void updateOrderStatus(ContextRefreshedEvent event) {
        List<Order> orderList = orderRepository.findByStatus(OrderStatus.ORDER);
        for(Order order : orderList) {
            long second = Duration.between(order.getOrderDate(), LocalDateTime.now()).getSeconds();
            if (second > 60 * 60 * 12) order.setStatus(OrderStatus.COMPLETE);
        }
    }
}