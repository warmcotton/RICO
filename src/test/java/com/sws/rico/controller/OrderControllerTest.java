package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.OrderException;
import com.sws.rico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderControllerTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemImgRepository itemImgRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private MockMvc mvc;
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
        Cart cart2 = Cart.getInstance(user2);

        CartItem cartItem1 = CartItem.getInstance(3, item1, cart2);
        CartItem cartItem2 = CartItem.getInstance(2, item2, cart1);

        userRepository.saveAll(asList(user1, user2));
        itemRepository.saveAll(asList(item1, item2));
        itemImgRepository.saveAll(asList(itemImg1, itemImg2, itemImg3));
        cartRepository.saveAll(asList(cart1, cart2));
        cartItemRepository.saveAll(asList(cartItem1, cartItem2));
    }


    @Test
    void orderItem() throws Exception {

        mvc.perform(get("/item/1/order/v2").with(user("jch@jch"))
                .param("quantity", "3"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void orderItem_exceed() throws Exception {

        mvc.perform(get("/item/1/order/v2").with(user("jch@jch"))
                        .param("quantity", "50"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(OrderException.class)));
    }


    @Test
    void orderItem_wrongUser() throws Exception {

        mvc.perform(get("/item/1/order/v2").with(user("sws@sws"))
                        .param("quantity", "3"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CartException.class)));
    }

    @Test
    void orderCart() throws Exception {
        List<Map<String, String>> l = new ArrayList<>();

        Map<String, String> m = new LinkedHashMap<>();

        m.put("id", "1");

        l.add(m);

        String json = objectMapper.writeValueAsString(l);

        mvc.perform(post("/cart/order/v2").with(user("sws@sws"))
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void orderCart_WN() throws Exception {
        List<Map<String, String>> l = new ArrayList<>();

        Map<String, String> m = new LinkedHashMap<>();

        m.put("id", "2");

        l.add(m);

        String json = objectMapper.writeValueAsString(l);

        mvc.perform(post("/cart/order/v2").with(user("sws@sws"))
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(OrderException.class)));
    }


    @Test
    void orderCart_N() throws Exception {
        List<Map<String, String>> l = new ArrayList<>();

        Map<String, String> m = new LinkedHashMap<>();

        m.put("id", "-1");

        l.add(m);

        String json = objectMapper.writeValueAsString(l);

        mvc.perform(post("/cart/order/v2").with(user("sws@sws"))
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(IllegalArgumentException.class)));
    }
}
