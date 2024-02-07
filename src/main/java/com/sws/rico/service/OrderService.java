package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.constant.OrderStatus;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
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
import java.util.Arrays;
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
    private final ItemRepository itemRepository;
    private final CommonUserService commonUserService;
    private final CommonItemService commonItemService;

    private void checkItemStatus(Long itemId, int count, String email) throws CustomException {
        if (!commonItemService.checkAndReduce(itemId, count)) throw new ItemException("수량 없음");
        Item item = itemRepository.findById(itemId).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
        if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품 주문 x");
    }

    public OrderDto orderItemv2(Long itemId, int count, String email) throws CustomException {
        if (!commonItemService.checkAndReduce(itemId, count)) throw new ItemException("수량 없음");
        Item item = itemRepository.findById(itemId).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
        if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품 주문 x");

        User user = userRepository.findByEmail(email).get();
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
        orderItemRepository.save(OrderItem.getInstance(item,newOrder, count, item.getPrice()));
        newOrder.setPrice(item.getPrice() * count);
        List<OrderItem> orderItemList = getOrderItems(newOrder);
        return OrderDto.getOrderDto(newOrder, orderItemList);
    }

    public OrderDto orderCartv2(String email) throws CustomException {
        User user = userRepository.findByEmail(email).get();
        Cart cart = checkCartItems(cartRepository.findByUser(user).get().getId());
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
        int total = 0;

        List<CartItem> cartItem = cartItemRepository.findByCart(cart);

        for (CartItem cit : cartItem) {
            Item item = cit.getItem();

            checkItemStatus(item.getId(), cit.getCount(), email);

            int price = item.getPrice()*cit.getCount();
            total += price;
            OrderItem newOrderItem = OrderItem.getInstance(item,newOrder,cit.getCount(),price);
            orderItemRepository.save(newOrderItem);
            cartItemRepository.delete(cit);
        }
        cartRepository.delete(cart);
        newOrder.setPrice(total);
        List<OrderItem> orderItemList = getOrderItems(newOrder);
        return OrderDto.getOrderDto(newOrder, orderItemList);
    }

    public Page<OrderDto> getOrderDtoPage(String email, Pageable page) {
        User user = userRepository.findByEmail(email).get();
        return orderRepository.findByUser(user, page)
                .map(order -> OrderDto.getOrderDto(order, getOrderItems(order)));
    }

    public void cancel(Long orderId, String email) throws CustomException {
        if(!commonUserService.validateUserOrder(orderId, email)) throw new OrderException("접근 권한 없음");
        Order order = getOrder(orderId);
        if(order.getStatus()== OrderStatus.CANCEL) throw new OrderException("이미 취소됨");
        else if (order.getStatus() == OrderStatus.COMPLETE) throw new OrderException("배송 완료됨");
        List<OrderItem> orderItemList = getOrderItems(order);
        for(OrderItem orderItem : orderItemList) {
            commonItemService.restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }

    public void deleteOrder(Long id) throws Exception {
        Order order = getOrder(id);
        if(order.getStatus()==OrderStatus.ORDER) throw new Exception("주문 취소 먼저");
        orderItemRepository.deleteAllByOrder(order);
        orderRepository.deleteById(id);
    }

    @EventListener
    public void updateOrderStatus(ContextRefreshedEvent event) {
        List<Order> orderList = orderRepository.findByStatus(OrderStatus.ORDER);
        for(Order order : orderList) {
            long second = Duration.between(order.getOrderDate(), LocalDateTime.now()).getSeconds();
            if (second > 60 * 60 * 12) order.setStatus(OrderStatus.COMPLETE);
        }
    }

    private Cart createCartByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.save(Cart.getInstance(user));
    }

    private List<Cart> checkCartItems(List<Long> cartIdList) throws CustomException {
        List<Cart> cartList_ = new ArrayList<>();

        for (Long cartid : cartIdList) {
            Cart cart = cartRepository.findById(cartid).get();
            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품 주문");
            for (CartItem cartItem : cartItems) {
                if (!commonItemService.checkAndReduce(cartItem.getItem().getId(), cartItem.getCount()))
                    throw new ItemException("수량 없음");
            }

            cartList_.add(cart);
        }

        return cartList_;
    }

    private Cart checkCartItems(Long cartId) throws CustomException {
        Cart cart = cartRepository.findById(cartId).get();
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품 주문");
        return cart;
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).get();
    }

    private List<OrderItem> getOrderItems(Order order) {
        return orderItemRepository.findByOrder(order);

    }

    //    public OrderDto orderCart(List<Long> cartIdList, String email) throws CustomException {
    //        List<Cart> checkedCartList = checkCartItems(cartIdList);
    //        User user = userRepository.findByEmail(email).get();
    //        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
    //
    //        int total = 0;
    //        for (Cart cart : checkedCartList) {
    //            List<CartItem> cartItem = cartItemRepository.findByCart(cart);
    //            int order_item_price = 0;
    //            for (CartItem cit : cartItem) {
    //                Item item = cit.getItem();
    //                if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품 주문 x");
    //                int price = item.getPrice()*cit.getCount();
    //                order_item_price += price;
    //                OrderItem newOrderItem = OrderItem.getInstance(item,newOrder,cit.getCount(),price);
    //                orderItemRepository.save(newOrderItem);
    //                cartItemRepository.delete(cit);
    //            }
    //            total += order_item_price;
    //            cartRepository.delete(cart);
    //        }
    //        newOrder.setPrice(total);
    //        List<OrderItem> orderItemList = getOrderItems(newOrder);
    //        return OrderDto.getOrderDto(newOrder, orderItemList);
    //    }

    //    public OrderDto orderItem(Long id, int quantity, String email) throws CustomException {
    //        Long cartId = createCartWithSingleItem(id, quantity, email).getId();
    //        return orderCart(Arrays.asList(cartId), email);
    //    }

    //    private Cart createCartWithSingleItem(Long itemId, int quantity, String email) throws CustomException {
    //        if (!commonItemService.checkAndReduce(itemId, quantity)) throw new ItemException("수량 없음");
    //        Cart newCart = createCartByEmail(email);
    //        Item item = itemRepository.findById(itemId).get();
    //        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
    //        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
    //        CartItem cartItem = CartItem.getInstance(quantity, item, newCart);
    //        cartItemRepository.save(cartItem);
    //        return newCart;
    //    }
}