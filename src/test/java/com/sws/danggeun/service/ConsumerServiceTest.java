package com.sws.danggeun.service;

import com.sws.danggeun.constant.ItemStatus;
import com.sws.danggeun.dto.CartDto;
import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.CartItemRepository;
import com.sws.danggeun.repository.CartRepository;
import com.sws.danggeun.repository.ItemRepository;
import com.sws.danggeun.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConsumerServiceTest {
    @Autowired
    ConsumerService consumerService;
    @Autowired
    ItemService itemService;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;
    void createItem(String email, String name, int price, int quantity, ItemStatus itemStatus) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setPrice(price);
        itemDto.setQuantity(quantity);
        itemDto.setItemStatus(itemStatus);
        itemService.saveItem(itemDto, email);
    }
    void createCart() {
        cartService.createCart("sws@naver.com");
        cartService.createCart("sws@naver.com");
        cartService.createCart("sws@naver.com");
    }
    @BeforeEach
    void init() {
        Random random = new Random();
        random.setSeed(1L);
        for(int i =0;i<10;i++) {
            createItem("sws@naver.com","product"+(i+1),10000*(random.nextInt(5)+1),random.nextInt(20)+10,ItemStatus.FOR_SALE);
        }
        createCart();
    }
    @Test
    void buySingleItem() throws Exception {
        int count = 10;
        System.out.println("______________________Before______________________");
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);
        consumerService.viewItemList().forEach(System.out::println);
        consumerService.viewCartList("sws@naver.com").forEach(System.out::println);
        Item before = itemRepository.findById(1L).get();
        consumerService.buySingleItem(1L,count, "sws@naver.com");
        Item after = itemRepository.findById(1L).get();
        System.out.println("______________________After______________________");
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);
        consumerService.viewItemList().forEach(System.out::println);
        consumerService.viewCartList("sws@naver.com").forEach(System.out::println);
        assertAll("buy", () -> assertEquals(before.getQuantity()-count,after.getQuantity())
                );
    }
    @Test
    void addItemsToCart() throws Exception {
        Cart cart = cartService.createCart("sws@naver.com");
        cartService.addItemToCart(1L,5, cart.getId());
        cartService.addItemToCart(2L,10, cart.getId());
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        cartDtoList.stream().forEach(System.out::println);
    }
    @Test
    void buyCarts() throws Exception {
        Cart cart = cartService.createCart("sws@naver.com");
        Cart cart1 = cartService.createCart("sws@naver.com");
        cartService.addItemToCart(1L,5, cart.getId());
        cartService.addItemToCart(2L,10, cart.getId());
        cartService.addItemToCart(1L,1, cart1.getId());
        cartService.addItemToCart(2L,1, cart1.getId());
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        cartDtoList.stream().forEach(System.out::println);
        consumerService.buyCarts(cartDtoList, "sws@naver.com");
        List<OrderDto> orders = consumerService.viewOrderList("sws@naver.com");
        orders.stream().forEach(System.out::println);
        List<CartDto> cartDtos = consumerService.viewCartList("sws@naver.com");
        cartDtos.stream().forEach(System.out::println);
    }
    @Test
    void cancelOrder() throws Exception {
        List<Cart> cartList = cartService.getCarts("sws@naver.com");
        long r = 3L;
        for(Cart cart : cartList) {
            cartService.addItemToCart(r,3, cart.getId());
            cartService.addItemToCart(r+3L,2, cart.getId());
            r = r+1L;
        }
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        cartDtoList.forEach(System.out::println);

        OrderDto orderDto = consumerService.buyCarts(cartDtoList, "sws@naver.com");
        consumerService.viewItemList().forEach(System.out::println);
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);

        consumerService.cancel(1L);
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);
        consumerService.viewItemList().forEach(System.out::println);
    }
    @Test
    void cancelOrderThenDeleteItem() throws Exception {
        List<Cart> cartList = cartService.getCarts("sws@naver.com");
        long r = 1L;
        for(Cart cart : cartList) {
            cartService.addItemToCart(r,3, cart.getId());
            cartService.addItemToCart(r+3L,2, cart.getId());
            r = r+1L;
        }
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        consumerService.buyCarts(cartDtoList, "sws@naver.com");
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);
        int before = consumerService.viewOrderList("sws@naver.com").get(0).getOrderItemDtoList().size();
        consumerService.cancel(1L);
        itemService.deleteItem(1L);
        consumerService.viewOrderList("sws@naver.com").forEach(System.out::println);
        int after = consumerService.viewOrderList("sws@naver.com").get(0).getOrderItemDtoList().size();
        assertEquals(1,before-after);
    }
    @Test
    void deleteOrderWithOutCancel() throws Exception {
        List<Cart> cartList = cartService.getCarts("sws@naver.com");
        long r = 1L;
        for(Cart cart : cartList) {
            cartService.addItemToCart(r,3, cart.getId());
            cartService.addItemToCart(r+3L,2, cart.getId());
            r = r+1L;
        }
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        consumerService.buyCarts(cartDtoList, "sws@naver.com");
        assertEquals("주문 취소 먼저", assertThrows(Exception.class, ()->orderService.deleteOrder(1L)).getMessage());
    }
    @Test
    void cancelAfterCancel() throws Exception {
        List<Cart> cartList = cartService.getCarts("sws@naver.com");
        long r = 1L;
        for(Cart cart : cartList) {
            cartService.addItemToCart(r,3, cart.getId());
            cartService.addItemToCart(r+3L,2, cart.getId());
            r = r+1L;
        }
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        consumerService.buyCarts(cartDtoList, "sws@naver.com");
        consumerService.viewItemList().forEach(System.out::println);
        consumerService.cancel(1L);
        consumerService.viewItemList().forEach(System.out::println);
        Assertions.assertThrows(Exception.class, () -> consumerService.cancel(1L));
    }
}