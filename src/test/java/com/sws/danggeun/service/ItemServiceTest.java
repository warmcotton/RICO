package com.sws.danggeun.service;

import com.sws.danggeun.constant.ItemStatus;
import com.sws.danggeun.dto.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ItemServiceTest {
    @Autowired private ItemService itemService;
    @Autowired private CartService cartService;
    String path = "src/test/resources/images/";
    String[] images = {"AMEB0802.JPG","APLD9975.JPG"};
    void createItem(String email, String name, int price, int quantity, ItemStatus itemStatus, List<MultipartFile> multipartFiles) throws IOException {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setPrice(price);
        itemDto.setQuantity(quantity);
        itemDto.setItemStatus(itemStatus);
        itemService.saveItem(itemDto, multipartFiles, email);
    }
    void createCart() {
        cartService.createCart("sws@naver.com");
        cartService.createCart("sws@naver.com");
        cartService.createCart("sws@naver.com");
    }

    @BeforeEach
    void init() throws IOException {
        Random random = new Random();
        random.setSeed(1L);
        List<MultipartFile> list = createMultiPart();
        for(int i =0;i<10;i++) {
            createItem("sws@naver.com","product"+(i+1),10000*(random.nextInt(5)+1),random.nextInt(20)+10,ItemStatus.FOR_SALE, list);
        }
        createCart();
    }

    List<MultipartFile> createMultiPart() throws IOException {
        List<MultipartFile> multipartFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            FileInputStream fis = new FileInputStream(path+images[i]);
            MockMultipartFile file = new MockMultipartFile("image"+i,images[i],"jpg",fis);
            multipartFiles.add(file);
        }
        return multipartFiles;
    }

    @Test
    void saveItemImg() throws IOException {

    }
}