package com.sws.danggeun.service;

import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.ItemImgRepository;
import com.sws.danggeun.repository.ItemRepository;
import com.sws.danggeun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository; //controller레이어로 이동 고려 -> createItem 에서 파라미터 email 대신 User
    private final ItemImgRepository itemImgRepository;
    //상품생성(판매)
    public Item createItem(ItemDto itemDto, String email) { //controller에서 dto 검사,
        User user = userRepository.findByEmail(email).get();
        Item newItem = Item.getInstance(itemDto.getName(),itemDto.getPrice(),
                itemDto.getQuantity(),itemDto.getItemStatus(),user);
        itemRepository.save(newItem);

        List<String> multi = new ArrayList<>(Arrays.asList("name-date.jpg","name.jpg","../pic","Y"));
        List<ItemImg> newItemImgList = temp(newItem, multi); //FileService - 멀티파일 리스트 받아서 List<ItemImg> 리턴

        itemImgRepository.saveAll(newItemImgList);

        return newItem;
    }
    private List<ItemImg> temp(Item item, List<String> multi) {
        List<ItemImg> itemImgList = new ArrayList<>();
        ItemImg itemImg = new ItemImg();
        itemImg.setImgName(multi.get(0));
        itemImg.setOriImgName(multi.get(1));
        itemImg.setImgUrl(multi.get(2));
        itemImg.setRepimgYn(multi.get(3));
        itemImg.setItem(item);
        itemImgList.add(itemImg);

        return itemImgList;
    }
    //상품조회
    public Item searchItem(Long id) {
        return itemRepository.findById(id).get();
    }

    //상품검색

    //아이템 수량 확인 : 상품 조회 -> 상품 수량 확인
    private int countItemQuantity(Long id) {
        Item item = searchItem(id);
        return item.getQuantity();
    }

    //상품 수량 차감 : 아이템수량 - 주문수량 비교
    public boolean checkAndReduce(long id, int quantity) {
        if (quantity > countItemQuantity(id)) return false;
        updateItemQuantity(id, quantity);
        return true;
    }

    //상품 quantity update
    private void updateItemQuantity(Long id, int quantity) {
        Item item = searchItem(id);
        int remain = item.getQuantity() - quantity;
        item.setQuantity(remain);
        if(remain == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        else item.setItemStatus(ItemStatus.FOR_SALE);

    }

}
