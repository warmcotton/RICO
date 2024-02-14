package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.entity.User;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.CategoryRepository;
import com.sws.rico.repository.ItemImgRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.UserRepository;
import com.sws.rico.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Collections;


import static com.sws.rico.constant.CategoryDto.*;
import static java.util.Arrays.asList;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ItemControllerTest {
    @Autowired
    private OrderService orderService;
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
    private JdbcTemplate jdbcTemplate;
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
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item ALTER COLUMN item_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE item_img ALTER COLUMN item_img_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE category ALTER COLUMN category_id RESTART WITH 1");

        User user1 = User.createSupplier("sws@sws","1111","sws",passwordEncoder);
        User user2 = User.createUser("jch@jch","1111","jch",passwordEncoder);

        Item item1 = getItem("뉴발 991",180000,100,ItemStatus.FOR_SALE,"상품 소개 영역","상품 상세 설명 영역",user1);
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
        ItemImg itemImg4 = ItemImg.getInstance("random_image_id4", "original4.png", "/images/**", "Y", item2);
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
    void getItemsByCategory() throws Exception {
        mvc.perform(get("/items/category?category=SNEAKERS"))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    void saveItem() throws Exception {
        final String contentType = "png"; //파일타입

        //Mock파일생성
        MockMultipartFile image1 = new MockMultipartFile(
                "itemFileList", //name
                "hoody-1" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "itemFileList", //name
                "hoody-2" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("hoody");
        itemDto.setPrice(69000);
        itemDto.setQuantity(10);
        itemDto.setBrief("상품 간단 소개");
        itemDto.setDescription("상품 상세 설명   ");
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        itemDto.setCategory(Collections.singletonList(CASUAL));

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(
                multipart(HttpMethod.POST,"/item")
                        .file(image1).file(image2).file(text).with(user("sws@sws").authorities(authority))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void saveItem_NoImgFile() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("hoody");
        itemDto.setPrice(69000);
        itemDto.setQuantity(10);
        itemDto.setBrief("상품 간단 소개");
        itemDto.setDescription("상품 상세 설명   ");
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        itemDto.setCategory(Collections.singletonList(CASUAL));

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(
                multipart(HttpMethod.POST,"/item")
                        .file(text).with(user("Supplier").authorities(authority))
        ).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(MissingServletRequestPartException.class)));
    }

    @Test
    void getMyItems() throws Exception {
        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(get("/user/myitems").with(user("sws@sws").authorities(authority)))
                .andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getUserItems() throws Exception {
        mvc.perform(get("/user/1/items")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getUserItems_NoLogin() throws Exception {
        mvc.perform(get("/user/1/items")).andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getItemDto_wrongItemId() throws Exception {
        mvc.perform(get("/item/100").with(user("sws@sws"))).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));
    }

    @Test
    void getItemDtoList() throws Exception {
        mvc.perform(get("/items")).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void updateItem() throws Exception {
        final String contentType = "png";

        MockMultipartFile image1 = new MockMultipartFile(
                "itemFileList", //name
                "hoody-2" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("hoody");
        itemDto.setPrice(69000);
        itemDto.setQuantity(10);
        itemDto.setBrief("상품 간단 소개");
        itemDto.setDescription("상품 상세 설명   ");
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        itemDto.setCategory(Collections.singletonList(CASUAL));

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(
                multipart(HttpMethod.PUT,"/item/1")
                        .file(image1).file(text).with(user("sws@sws").authorities(authority))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void updateItem_wrongUser() throws Exception {
        final String contentType = "png";

        MockMultipartFile image1 = new MockMultipartFile(
                "itemFileList", //name
                "hoody-2" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("hoody");
        itemDto.setPrice(69000);
        itemDto.setQuantity(10);
        itemDto.setBrief("상품 간단 소개");
        itemDto.setDescription("상품 상세 설명   ");
        itemDto.setItemStatus(ItemStatus.FOR_SALE);
        itemDto.setCategory(Collections.singletonList(CASUAL));


        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(
                multipart(HttpMethod.PUT,"/item/1")
                        .file(image1).file(text).with(user("Supplier").authorities(authority))
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));

    }

    @Test
    void deleteItem() throws Exception {
        orderService.orderItem(1L, 3, "jch@jch");

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(delete("/item/1").with(user("sws@sws").authorities(authority)))
                .andDo(print()).andExpect(status().isOk());

    }

    @Test
    void deleteItem_wrongUser() throws Exception {
        orderService.orderItem(1L, 3, "jch@jch");

        GrantedAuthority authority = new SimpleGrantedAuthority("SUPPLIER");

        mvc.perform(delete("/item/1").with(user("Supplier").authorities(authority)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));

    }

}