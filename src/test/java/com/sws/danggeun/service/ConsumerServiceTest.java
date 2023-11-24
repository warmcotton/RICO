package com.sws.danggeun.service;

import com.sws.danggeun.dto.CartDto;
import com.sws.danggeun.dto.CartItemDto;
import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.CartItemRepository;
import com.sws.danggeun.repository.CartRepository;
import com.sws.danggeun.repository.ItemRepository;
import com.sws.danggeun.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
    Item create(String email) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("ProductA");
        itemDto.setPrice(10000);
        itemDto.setQuantity(10);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        Item i = itemService.saveItem(itemDto, email);

        return i;
    }
    Item create1(String email) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("ProductB");
        itemDto.setPrice(50000);
        itemDto.setQuantity(20);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        Item i = itemService.saveItem(itemDto, email);

        return i;
    }

    @Test
    void buySingleItem() throws Exception {
        Item newitem = create("sws@naver.com");

        List<OrderDto> order = consumerService.viewOrderList("sws@naver.com");
        List<ItemDto> item = consumerService.viewItemList();
        List<CartDto> cart = consumerService.viewCartList("sws@naver.com");

        order.forEach(System.out::println);
        item.forEach(System.out::println);
        cart.forEach(System.out::println);

        consumerService.buySingleItem(newitem.getId(),10, "sws@naver.com");

        Item update = itemRepository.findById(newitem.getId()).get();

        List<OrderDto> orders = consumerService.viewOrderList("sws@naver.com");
        List<ItemDto> items = consumerService.viewItemList();
        List<CartDto> carts = consumerService.viewCartList("sws@naver.com");

        orders.forEach(System.out::println);
        items.forEach(System.out::println);
        carts.forEach(System.out::println);

        assertAll("buy", () -> assertEquals(0,update.getQuantity())
                );
    }

    @Test
    void addItemsToCart() {
        Cart cart = cartService.createCart("sws@naver.com");
        Item item = create("sws@naver.com");
        Item item1 = create1("sws@naver.com");
        cartService.addItem(item.getId(),5, cart.getId());
        cartService.addItem(item1.getId(),10, cart.getId());

        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        cartDtoList.stream().forEach(System.out::println);
    }

    @Test
    void buyCarts() throws Exception {
        Cart cart = cartService.createCart("sws@naver.com");
        Item item = create("sws@naver.com");
        Item item1 = create1("sws@naver.com");
        Cart cart1 = cartService.createCart("sws@naver.com");
        cartService.addItem(item.getId(),5, cart.getId());
        cartService.addItem(item1.getId(),10, cart.getId());
        cartService.addItem(item.getId(),1, cart1.getId());
        cartService.addItem(item1.getId(),1, cart1.getId());
        List<CartDto> cartDtoList = consumerService.viewCartList("sws@naver.com");
        cartDtoList.stream().forEach(System.out::println);

        consumerService.buyCarts(cartDtoList, "sws@naver.com");

        List<OrderDto> orders = consumerService.viewOrderList("sws@naver.com");
        orders.stream().forEach(System.out::println);
        List<CartDto> cartDtos = consumerService.viewCartList("sws@naver.com");
        cartDtos.stream().forEach(System.out::println);
    }
}