package com.sws.danggeun.service;

import com.sws.danggeun.dto.ItemDto;
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
    Item create(String email) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("ProductA");
        itemDto.setPrice(10000);
        itemDto.setQuantity(10);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        Item i = itemService.createItem(itemDto, email);

        return i;
    }

    @Test
    void buySingleItem() throws Exception {
        Item item = create("sws@naver.com");

        consumerService.buySingleItem(item.getId(),5, "sws@naver.com");
        Item update = itemRepository.findById(item.getId()).get();

        List<Order> orders = orderRepository.findAll(); // service로 이동시키기
        List<Item> items = itemRepository.findAll();
        List<CartItem> cartitems = cartItemRepository.findAll();
        List<Cart> carts = cartRepository.findAll();

        for(Order o : orders) {
            System.out.println(o);
        }
        for(Item i : items) {
            System.out.println(i);
        }
        for(Cart c : carts) {
            System.out.println(c);
        }
        for(CartItem ci : cartitems) {
            System.out.println(ci);
        }

        assertAll("buy", () -> assertEquals(5,update.getQuantity())
                );
    }
}