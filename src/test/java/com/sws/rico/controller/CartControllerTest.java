package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.transaction.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class CartControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ItemImgRepository itemImgRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        User user1 = User.getInstance("sws@sws","1111","sws",passwordEncoder);
        User user2 = User.getInstance("jch@jch","1111","jch",passwordEncoder);

        Item item1 = Item.getInstance("item1",50000,10, ItemStatus.FOR_SALE, user1);
        Item item2 = Item.getInstance("item2",10000,5, ItemStatus.FOR_SALE, user2);

        ItemImg itemImg1 = ItemImg.getInstance("random_image_id1", "original1.png", "/images/**", "Y", item1);
        ItemImg itemImg2 = ItemImg.getInstance("random_image_id2", "original2.png", "/images/**", "N", item1);
        ItemImg itemImg3 = ItemImg.getInstance("random_image_id3", "original3.png", "/images/**", "Y", item2);

        Cart cart1 = Cart.getInstance(user1);
        Cart cart2 = Cart.getInstance(user1);

        CartItem cartItem1 = CartItem.getInstance(3, item1, cart1);
        CartItem cartItem2 = CartItem.getInstance(2, item2, cart1);

        userRepository.saveAll(asList(user1, user2));
        itemRepository.saveAll(asList(item1, item2));
        itemImgRepository.saveAll(asList(itemImg1, itemImg2, itemImg3));
        cartRepository.saveAll(asList(cart1, cart2));
        cartItemRepository.saveAll(asList(cartItem1, cartItem2));
    }

    @Test
    void getCarts() throws Exception {
        mvc.perform(get("/carts").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCart() throws Exception {
        mvc.perform(get("/cart/1").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCartItem() throws Exception {
        mvc.perform(get("/cartItem/1").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void createCart() throws Exception {
        mvc.perform(post("/cart/create").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void addMyItem() throws Exception {
        Map<String, Object> json = new LinkedHashMap<>();

        json.put("itemId",1L);
        json.put("quantity", 3);

        String content = objectMapper.writeValueAsString(json);

        mvc.perform(post("/cart/1/add").with(user("sws@sws"))
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CartException.class)));

    }

    @Test
    void updateCartItem() throws Exception {
        mvc.perform(put("/cartItem/1").with(user("sws@sws"))
                .param("count", "2"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void updateCartItem_ClientError() throws Exception {
        mvc.perform(put("/cartItem/1").with(user("sws@sws"))
                .param("count", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(IllegalArgumentException.class)));

    }

    @Test
    void deleteCart() throws Exception {
        mvc.perform(delete("/cart/1").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteCartItem() throws Exception {
        mvc.perform(delete("/cartItem/1").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isOk());
    }
}