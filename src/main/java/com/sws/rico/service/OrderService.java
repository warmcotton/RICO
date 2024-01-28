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

import static java.util.stream.Collectors.groupingBy;

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


    //조회
    public Order getOrder(Long id) {
        return orderRepository.findById(id).get();
    }

    //주문
//    public Order order(List<Cart> cartList, String email) throws CustomException { //상품 수량 확인 -> ( 아이템 수량 차감 -> 주문 ) 하나의 트랜잭션
//        User user = userRepository.findByEmail(email).get();
//        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
//        int total = 0;
//        for (Cart cart : cartList) {
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
//        return newOrder;
//    }

    public OrderDto orderCart(List<Long> cartIdList, String email) throws CustomException {
        List<Cart> checkedCartList = checkCartItems(cartIdList);

        User user = userRepository.findByEmail(email).get();
        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));

        int total = 0;
        for (Cart cart : checkedCartList) {
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

        List<OrderItem> orderItemList = getOrderItems(newOrder);
        return OrderDto.getOrderDto(newOrder, orderItemList);
    }


    public OrderDto orderItem(Long id, int quantity, String email) throws CustomException {
        Long cartId = createCartWithSingleItem(id, quantity, email).getId();
        return orderCart(Arrays.asList(cartId), email);
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


    private boolean checkAndReduce(long itemId, int quantity) throws CustomException {
        if (quantity > countItemQuantity(itemId)) throw new OrderException();
        updateItemQuantity(itemId, quantity);
        return true;
    }

    private void updateItemQuantity(Long id, int quantity) {
        Item item = itemRepository.findById(id).get();
        int remain = item.getQuantity() - quantity;
        item.setQuantity(remain);
        if(remain == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        else item.setItemStatus(ItemStatus.FOR_SALE);
    }
    //아이템 수량 확인 : 상품 조회 -> 상품 수량 확인
    private int countItemQuantity(Long id) {
        Item item = itemRepository.findById(id).get();
        return item.getQuantity();
    }

    private Cart createCartWithSingleItem(Long itemId, int quantity, String email) throws CustomException {
        if (!checkAndReduce(itemId, quantity)) throw new ItemException("수량 없음");
        Cart newCart = createCartByEmail(email);
        Item item = itemRepository.findById(itemId).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
        CartItem cartItem = CartItem.getInstance(quantity, item, newCart);
        cartItemRepository.save(cartItem);
        return newCart;
    }

    private Cart createCartByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.save(Cart.getInstance(user));
    }

    private List<Cart> checkCartItems(List<Long> cartIdList) throws CustomException {
        List<Cart> cartList_ = new ArrayList<>();

        for(Long cartid : cartIdList) {
            Cart cart = cartRepository.findById(cartid).get();
            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
            if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품 주문");
            for (CartItem cartItem: cartItems) {
                if (!checkAndReduce(cartItem.getItem().getId(), cartItem.getCount()))
                    throw new ItemException("수량 없음");
            }
            cartList_.add(cart);
        }

        return cartList_;
    }

    public void cancel(Long orderId, String email) throws CustomException {
        if(!checkUser(orderId, email)) throw new OrderException("접근 권한 없음");
        Order order = getOrder(orderId);
        if(order.getStatus()== OrderStatus.CANCEL) throw new OrderException("이미 취소됨");
        else if (order.getStatus() == OrderStatus.COMPLETE) throw new OrderException("배송 완료됨");
        List<OrderItem> orderItemList = getOrderItems(order);
        for(OrderItem orderItem : orderItemList) {
            restore(orderItem.getItem().getId(),orderItem.getCount());
        }
        order.setStatus(OrderStatus.CANCEL);
    }

    protected void restore(long id, int quantity) {
        Item item = itemRepository.findById(id).get();
        item.setQuantity(item.getQuantity()+quantity);
    }

}
