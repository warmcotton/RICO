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

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(rollbackOn = {CustomException.class})
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
//        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
//        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("상품 판매중이 아닙니다.");
//        if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품은 주문할 수 없습니다.");
//        if (!commonItemService.checkItemStatus(itemId, count)) throw new ItemException("수량 없음");
        Item item = commonOrderService.checkItemStatus(itemId, count, email);
//        item.setQuantity(item.getQuantity() - count);
//        if(item.getQuantity() - count == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        item = commonOrderService.reduceItemQuantity(item, count);
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
//        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
//        orderItemRepository.save(OrderItem.getInstance(item,newOrder, count, item.getPrice()));
//        newOrder.setPrice(item.getPrice() * count);
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

//        Order newOrder = orderRepository.save(Order.getInstance(user, OrderStatus.ORDER));
        Order newOrder = commonOrderService.saveOrder(total, email);
        for (CartItem cit : cartItemRepository.findByCart(cart)) {
            Item item = cit.getItem();
//            if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("상품 판매중이 아닙니다.");
//            if(item.getUser().getEmail().equals(email)) throw new OrderException("본인이 판매하는 상품은 주문할 수 없습니다.");
//            if (!commonItemService.checkItemStatus(item.getId(), cit.getCount())) throw new ItemException("상품 수량이 없습니다.");
            item = commonOrderService.checkItemStatus(item.getId(), cit.getCount(), email);
//            item.setQuantity(item.getQuantity() - cit.getCount());
//            if(item.getQuantity() - cit.getCount() == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
            item = commonOrderService.reduceItemQuantity(item,  cit.getCount());

            newOrder = commonOrderService.saveOrderItem(newOrder, item, cit.getCount());

            total += item.getPrice()*cit.getCount();
//            OrderItem newOrderItem = OrderItem.getInstance(item,newOrder,cit.getCount(),item.getPrice()*cit.getCount());
//            orderItemRepository.save(newOrderItem);
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

//    private Cart createCartByEmail(String email) {
//        User user = userRepository.findByEmail(email).get();
//        return cartRepository.save(Cart.getInstance(user));
//    }
//
//    private List<Cart> checkCartItems(List<Long> cartIdList) throws CustomException {
//        List<Cart> cartList_ = new ArrayList<>();
//
//        for (Long cartid : cartIdList) {
//            Cart cart = cartRepository.findById(cartid).get();
//            List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//            if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품 주문");
//            for (CartItem cartItem : cartItems) {
//                if (!commonItemService.checkItemStatus(cartItem.getItem().getId(), cartItem.getCount()))
//                    throw new ItemException("수량 없음");
//            }
//
//            cartList_.add(cart);
//        }
//
//        return cartList_;
//    }
//
//    private Cart checkCartItems(Long cartId) throws CustomException {
//        Cart cart = cartRepository.findById(cartId).get();
//        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
//        if (cartItems.size() == 0) throw new OrderException("최소 1개 이상의 상품 주문");
//        return cart;
//    }
//
//    private Order getOrder(Long orderId) {
//        return orderRepository.findById(orderId).get();
//    }

}