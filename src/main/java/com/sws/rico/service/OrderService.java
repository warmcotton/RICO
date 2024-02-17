package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.*;
import com.sws.rico.mapper.OrderMapper;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;

    public OrderDto getOrderDto(Long orderId, String email) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if (!order.getUser().getEmail().equals(email)) throw new OrderException("접근 권한 없습니다.");

        return orderMapper.toOrderDto(order);
    }

    public List<OrderDto> getOrderDtoPage(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        return orderRepository.findTop50ByUserOrderByOrderDateDesc(user)
                .stream().map(orderMapper::toOrderDto).collect(toList());
    }

    public OrderDto orderItem(Long itemId, int count, String email) {
        Item item = checkItemStatus(itemId, count, email);
        reduceItemQuantity(item, count);
        Order newOrder = saveOrder(email);
        saveOrderItem(newOrder, item, count);

        return orderMapper.toOrderDto(newOrder);
    }

    public OrderDto orderCartv2(String email) {
        int total = 0;
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니가 생성되지 않았습니다."));
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품을 주문해야 합니다.");

        Order newOrder = saveOrder(email);
        for (CartItem cit : cartItemRepository.findByCart(cart)) {
            Item item = cit.getItem();
            item = checkItemStatus(item.getId(), cit.getCount(), email);
            reduceItemQuantity(item,  cit.getCount());

            saveOrderItem(newOrder, item, cit.getCount());

            total += item.getPrice()*cit.getCount();
            cartItemRepository.delete(cit);
        }
        cartRepository.delete(cart);
        newOrder.setPrice(total);

        return orderMapper.toOrderDto(newOrder);
    }

    public void cancel(Long orderId, String email) {
        if(!validateUserOrder(orderId, email)) throw new OrderException("접근 권한이 없습니다.");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if(order.getStatus()== OrderStatus.CANCEL) throw new OrderException("주문이 이미 취소되었습니다.");
        else if (order.getStatus() == OrderStatus.COMPLETE) throw new OrderException("배송이 이미 완료되었습니다.");
        List<OrderItem> orderItemList = orderItemRepository.findByOrder(order);
        for(OrderItem orderItem : orderItemList) {
            restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }

    public void deleteOrder(Long orderId, String email) {
        if(!validateUserOrder(orderId, email)) throw new OrderException("접근 권한이 없습니다");
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderException("주문 정보가 없습니다."));
        if(order.getStatus()==OrderStatus.ORDER) throw new OrderException("주문 취소가 되지 않았습니다.");
        orderItemRepository.deleteAllByOrder(order);
        orderRepository.deleteById(orderId);
    }

    private Item checkItemStatus(Long itemId, int count, String email) {
        Item item = itemRepository.findByIdWithPessimisticWriteLock(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("상품 판매중이 아닙니다.");
        if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품은 주문할 수 없습니다.");
        boolean ct = count <= itemRepository.findById(itemId).get().getQuantity();
        if (!ct) throw new ItemException("수량 없음");
        return item;
    }

    private void reduceItemQuantity(Item item, int count) {
        item.setQuantity(item.getQuantity() - count);
        if(item.getQuantity() - count == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
    }

    private Order saveOrder(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        return orderRepository.save(Order.createOrder(user, OrderStatus.ORDER));
    }

    private void saveOrderItem(Order order, Item item, int count) {
        orderItemRepository.save(OrderItem.createOrderItem(item, order, count, item.getPrice()));
        order.setPrice(item.getPrice() * count);
    }

    private void restore(long itemId, int quantity) {
        Item item = itemRepository.findById(itemId).get();
        item.setQuantity(item.getQuantity()+quantity);
    }

    private boolean validateUserOrder(Long orderId, String email) {
        Order order = orderRepository.findById(orderId).get();
        return email.equals(order.getUser().getEmail());
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