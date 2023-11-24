package com.sws.danggeun.service;

import com.sws.danggeun.entity.Cart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceTest {
    @Autowired
    CartService cartService;

    Cart returnCart() {
        Cart cart = cartService.createCart("sws@naver.com");
        return cart;
    }

    @Test
    void searchCartAfterCreateCart() {
      Cart create = returnCart(); //1 transaction
      Cart search = cartService.getCart(create.getId()); //1 transaction

      assertAll("create", () -> assertEquals(create.getId(),search.getId()));
    }

    @Test
    void createCart() {
        Cart cart = cartService.createCart("sws@naver.com");
        assertEquals(1L,cart.getId());
    }

    @Test
    void deleteCart() {
    }
}