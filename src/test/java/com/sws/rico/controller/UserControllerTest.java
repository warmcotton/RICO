package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.entity.User;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.CategoryRepository;
import com.sws.rico.repository.ItemImgRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.sws.rico.constant.CategoryDto.*;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ItemImgRepository itemImgRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static Item getItem(String name, int price, int quantity, ItemStatus itemStatus, String brief, String desciprtion, User user) {
        Item item = new Item();
        item.setName(name); //possible by reflection
        item.setPrice(price);
        item.setQuantity(quantity);
        item.setItemStatus(itemStatus);
        item.setBrief(brief);
        item.setDescription(desciprtion);
        item.setUser(user);
        return item;
    }
    @BeforeEach()
    void init() {
        User user1 = User.createUser("sws@sws","1111","sws",passwordEncoder);
        User user2 = User.createUser("jch@jch","1111","jch",passwordEncoder);

        Item item1 = getItem("뉴발 991",180000,100, ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item2 = getItem("나이키 덩크",200000,80,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item3 = getItem("캐주얼 자켓",160000,120,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item4 = getItem("숏 패딩",170000,110,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item5 = getItem("스포츠 양말",190000,130,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
        Item item6 = getItem("양말",150000,90,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);

        CategoryWrapper c1 = CategoryWrapper.getInstance(SNEAKERS, item1);
        CategoryWrapper c2 = CategoryWrapper.getInstance(RUNNING, item1);
        CategoryWrapper c3 = CategoryWrapper.getInstance(SNEAKERS, item2);
        CategoryWrapper c4 = CategoryWrapper.getInstance(SPORTSWEAR, item2);
        CategoryWrapper c5 = CategoryWrapper.getInstance(CASUAL, item3);
        CategoryWrapper c6 = CategoryWrapper.getInstance(OUTERWEAR, item3);
        CategoryWrapper c7 = CategoryWrapper.getInstance(OUTERWEAR, item4);
        CategoryWrapper c8 = CategoryWrapper.getInstance(PADDING, item4);
        CategoryWrapper c9 = CategoryWrapper.getInstance(SPORTSWEAR, item5);
        CategoryWrapper c10 = CategoryWrapper.getInstance(SOCKS, item5);

        ItemImg itemImg1 = ItemImg.getInstance("random_image_id1", "original1.png", "/images/**", "Y", item1);
        ItemImg itemImg2 = ItemImg.getInstance("random_image_id2", "original2.png", "/images/**", "N", item1);
        ItemImg itemImg3 = ItemImg.getInstance("random_image_id3", "original3.png", "/images/**", "Y", item2);
        ItemImg itemImg4 = ItemImg.getInstance("random_image_id4", "original4.png", "/images/**", "N", item2);
        ItemImg itemImg5 = ItemImg.getInstance("random_image_id5", "original5.png", "/images/**", "Y", item3);
        ItemImg itemImg6 = ItemImg.getInstance("random_image_id6", "original6.png", "/images/**", "N", item3);
        ItemImg itemImg7 = ItemImg.getInstance("random_image_id7", "original7.png", "/images/**", "Y", item4);
        ItemImg itemImg8 = ItemImg.getInstance("random_image_id8", "original8.png", "/images/**", "N", item4);
        ItemImg itemImg9 = ItemImg.getInstance("random_image_id9", "original9.png", "/images/**", "Y", item5);
        ItemImg itemImg10 = ItemImg.getInstance("random_image_id10", "original10.png", "/images/**", "N", item5);
        ItemImg itemImg11 = ItemImg.getInstance("random_image_id11", "original11.png", "/images/**", "Y", item6);
        ItemImg itemImg12 = ItemImg.getInstance("random_image_id12", "original12.png", "/images/**", "N", item6);

        userRepository.saveAll(asList(user1, user2));
        itemRepository.saveAll(asList(item1, item2, item3, item4, item5, item6));
        categoryRepository.saveAll(asSet(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10));
        itemImgRepository.saveAll(asList(itemImg1, itemImg2, itemImg3, itemImg4, itemImg5, itemImg6, itemImg7, itemImg8, itemImg9, itemImg10,
                itemImg11, itemImg12));
    }

    @Test
    void submitReview() throws Exception {
        Map<String, String> json = new LinkedHashMap<>();

        json.put("itemId", "1");
        json.put("rating", "3");
        json.put("review", "review review review review review");

        String content = objectMapper.writeValueAsString(json);


        mvc.perform(post("/review").content(content).contentType(MediaType.APPLICATION_JSON).with(user("sws@sws"))).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void submitReview_NOUser() throws Exception {
        Map<String, String> json = new LinkedHashMap<>();

        json.put("itemId", "1");
        json.put("rating", "3");
        json.put("review", "review review review review review");

        String content = objectMapper.writeValueAsString(json);


        mvc.perform(post("/review").content(content).contentType(MediaType.APPLICATION_JSON)).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getReview() throws Exception {
        mvc.perform(get("/reviews/1")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getReview_NoItem() throws Exception {
        mvc.perform(get("/reviews/7")).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));
    }
}