package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.constant.Role;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.sws.rico.constant.CategoryDto.*;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import javax.transaction.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    private CategoryRepository categoryRepository;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private static Item getItem(String name, int price, int quantity, ItemStatus itemStatus, String brief, String desciprtion, User user) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setItemStatus(itemStatus);
        item.setBrief(brief);
        item.setDescription(desciprtion);
        item.setUser(user);
        return item;
    }
    @BeforeEach
    void init() {
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN item_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item_img ALTER COLUMN item_img_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE cart ALTER COLUMN cart_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE cart_item ALTER COLUMN cart_item_id RESTART WITH 1");

        User user1 = User.createUser("sws@sws","1111","sws", Role.SUPPLIER, passwordEncoder);
        User user2 = User.createUser("jch@jch","1111","jch", Role.USER, passwordEncoder);

        Item item1 = getItem("뉴발 991",180000,100,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item2 = getItem("나이키 덩크",200000,80,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item3 = getItem("캐주얼 자켓",160000,120,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item4 = getItem("숏 패딩",170000,110,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item5 = getItem("스포츠 양말",190000,130,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item6 = getItem("양말",150000,90,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);

        CategoryWrapper c1 = CategoryWrapper.createCategoryWrapper(SNEAKERS, item1);
        CategoryWrapper c2 = CategoryWrapper.createCategoryWrapper(RUNNING, item1);
        CategoryWrapper c3 = CategoryWrapper.createCategoryWrapper(SNEAKERS, item2);
        CategoryWrapper c4 = CategoryWrapper.createCategoryWrapper(SPORTSWEAR, item2);
        CategoryWrapper c5 = CategoryWrapper.createCategoryWrapper(CASUAL, item3);
        CategoryWrapper c6 = CategoryWrapper.createCategoryWrapper(OUTERWEAR, item3);
        CategoryWrapper c7 = CategoryWrapper.createCategoryWrapper(OUTERWEAR, item4);
        CategoryWrapper c8 = CategoryWrapper.createCategoryWrapper(PADDING, item4);
        CategoryWrapper c9 = CategoryWrapper.createCategoryWrapper(SPORTSWEAR, item5);
        CategoryWrapper c10 = CategoryWrapper.createCategoryWrapper(SOCKS, item5);

        ItemImg itemImg1 = ItemImg.createItemImg("random_image_id1", "original1.png", "/images/**", "Y", item1);
        ItemImg itemImg2 = ItemImg.createItemImg("random_image_id2", "original2.png", "/images/**", "N", item1);
        ItemImg itemImg3 = ItemImg.createItemImg("random_image_id3", "original3.png", "/images/**", "Y", item2);
        ItemImg itemImg4 = ItemImg.createItemImg("random_image_id4", "original4.png", "/images/**", "Y", item2);
        ItemImg itemImg5 = ItemImg.createItemImg("random_image_id5", "original5.png", "/images/**", "Y", item3);
        ItemImg itemImg6 = ItemImg.createItemImg("random_image_id6", "original6.png", "/images/**", "N", item3);
        ItemImg itemImg7 = ItemImg.createItemImg("random_image_id7", "original7.png", "/images/**", "Y", item4);
        ItemImg itemImg8 = ItemImg.createItemImg("random_image_id8", "original8.png", "/images/**", "N", item4);
        ItemImg itemImg9 = ItemImg.createItemImg("random_image_id9", "original9.png", "/images/**", "Y", item5);
        ItemImg itemImg10 = ItemImg.createItemImg("random_image_id10", "original10.png", "/images/**", "N", item5);
        ItemImg itemImg11 = ItemImg.createItemImg("random_image_id11", "original11.png", "/images/**", "Y", item6);
        ItemImg itemImg12 = ItemImg.createItemImg("random_image_id12", "original12.png", "/images/**", "N", item6);

        Cart cart1 = Cart.createCart(user2);

        CartItem cartItem1 = CartItem.createCartItem(3, item1, cart1);
        CartItem cartItem2 = CartItem.createCartItem(2, item2, cart1);

        userRepository.saveAll(asList(user1, user2));
        itemRepository.saveAll(asList(item1, item2, item3, item4, item5, item6));
        categoryRepository.saveAll(asSet(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10));
        itemImgRepository.saveAll(asList(itemImg1, itemImg2, itemImg3, itemImg4, itemImg5, itemImg6, itemImg7, itemImg8, itemImg9, itemImg10,
                itemImg11, itemImg12));
        cartRepository.save(cart1);
        cartItemRepository.saveAll(asList(cartItem1, cartItem2));
    }

    @Test
    void orderItem() throws Exception {
        Map<String, String> order = new LinkedHashMap<>();
        order.put("item_id","1");
        order.put("count", "100");

        String json = objectMapper.writeValueAsString(order);

        mvc.perform(post("/order/item").with(user("jch@jch"))
                .content(json).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void orderItem_exceed() throws Exception {
        Map<String, String> order = new LinkedHashMap<>();
        order.put("item_id","1");
        order.put("count", "105");
        String json = objectMapper.writeValueAsString(order);

        mvc.perform(post("/order/item").with(user("jch@jch"))
                        .content(json).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));
    }


    @Test
    void orderItem_wrongUser() throws Exception {
        Map<String, String> order = new LinkedHashMap<>();
        order.put("item_id","1");
        order.put("count", "100");
        String json = objectMapper.writeValueAsString(order);

        mvc.perform(post("/order/item").with(user("sws@sws"))
                        .content(json).contentType("application/json"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(OrderException.class)));
    }

    @Test
    void orderCart() throws Exception {
        mvc.perform(get("/order/cart").with(user("jch@jch")))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void orderCart_N() throws Exception {
        mvc.perform(get("/order/cart").with(user("sws@sws")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(CartException.class)));
    }
}
