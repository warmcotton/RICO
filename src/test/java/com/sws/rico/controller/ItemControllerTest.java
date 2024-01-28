package com.sws.rico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.entity.User;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.ItemImgRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.UserRepository;
import com.sws.rico.service.ConsumerService;
import com.sws.rico.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


import javax.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;


import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
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
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach()
    void init() {
        User user1 = User.getInstance("sws@sws","1111","sws",passwordEncoder);
        User user2 = User.getInstance("jch@jch","1111","jch",passwordEncoder);

        Item item1 = Item.getInstance("item1",50000,10, ItemStatus.FOR_SALE, user1);
        Item item2 = Item.getInstance("item2",10000,5, ItemStatus.FOR_SALE, user2);

        ItemImg itemImg1 = ItemImg.getInstance("random_image_id1", "original1.png", "/images/**", "Y", item1);
        ItemImg itemImg2 = ItemImg.getInstance("random_image_id2", "original2.png", "/images/**", "N", item1);
        ItemImg itemImg3 = ItemImg.getInstance("random_image_id3", "original3.png", "/images/**", "Y", item2);

        userRepository.saveAll(asList(user1, user2));
        itemRepository.saveAll(asList(item1, item2));
        itemImgRepository.saveAll(asList(itemImg1, itemImg2, itemImg3));


    }

    @Test
    void saveItem() throws Exception {
        final String contentType = "png"; //파일타입

        //Mock파일생성
        MockMultipartFile image1 = new MockMultipartFile(
                "itemFileList", //name
                "s24-1" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        MockMultipartFile image2 = new MockMultipartFile(
                "itemFileList", //name
                "s24-2" + "." + contentType, //originalFilename
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("galaxy s24");
        itemDto.setPrice(690000);
        itemDto.setQuantity(2);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));


        mvc.perform(
                multipart(HttpMethod.POST,"/item")
                        .file(image1).file(image2).file(text).with(user("sws@sws"))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void saveItem_NoImgFile() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("galaxy s24");
        itemDto.setPrice(690000);
        itemDto.setQuantity(2);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));


        mvc.perform(
                multipart(HttpMethod.POST,"/item")
                        .file(text).with(user("sws@sws"))
        ).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(MissingServletRequestPartException.class)));
    }

    @Test
    void getMyItems() throws Exception {
        mvc.perform(get("/user/myitems").with(user("sws@sws"))).andDo(print()).andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getUserItems() throws Exception {
        mvc.perform(get("/user/1/items")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void getUserItems_NoLogin() throws Exception {
        mvc.perform(get("/user/1/items")).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void getItemDto_wrongItemId() throws Exception {
        mvc.perform(get("/item/100").with(user("sws@sws"))).andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(NoSuchElementException.class)));
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
                "itemFileList",
                "s24-1" + "." + contentType,
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item1_update");
        itemDto.setPrice(100000);
        itemDto.setQuantity(5);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        mvc.perform(
                multipart(HttpMethod.PUT,"/item/1")
                        .file(image1).file(text).with(user("sws@sws"))
        ).andDo(print()).andExpect(status().isOk());
    }

    @Test
    void updateItem_wrongUser() throws Exception {
        final String contentType = "png";

        MockMultipartFile image1 = new MockMultipartFile(
                "itemFileList",
                "s24-1" + "." + contentType,
                contentType,
                "raw image bytes".getBytes()
        );

        ItemDto itemDto = new ItemDto();
        itemDto.setName("item1_update");
        itemDto.setPrice(100000);
        itemDto.setQuantity(5);
        itemDto.setItemStatus(ItemStatus.FOR_SALE);

        String content = objectMapper.writeValueAsString(itemDto);

        MockMultipartFile text = new MockMultipartFile("itemDto", "itemDto", "application/json", content.getBytes(StandardCharsets.UTF_8));

        mvc.perform(
                multipart(HttpMethod.PUT,"/item/1")
                        .file(image1).file(text).with(user("jch@jch"))
        ).andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));

    }

    @Test
    void deleteItem() throws Exception {
        orderService.orderItem(1L, 3, "sws@sws");

        mvc.perform(delete("/item/1").with(user("sws@sws")))
                .andDo(print()).andExpect(status().isOk());

    }

    @Test
    void deleteItem_wrongUser() throws Exception {
        orderService.orderItem(1L, 3, "sws@sws");

        mvc.perform(delete("/item/1").with(user("jch@jch")))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException().getClass().isAssignableFrom(ItemException.class)));

    }

}