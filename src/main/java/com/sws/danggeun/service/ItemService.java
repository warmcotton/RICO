package com.sws.danggeun.service;

import com.sws.danggeun.constant.ItemStatus;
import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository; //controller레이어로 이동 고려 -> createItem 에서 파라미터 email 대신 User
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    //상품생성(판매)
    public Item saveItem(ItemDto itemDto, String email) { //controller에서 dto 검사,
        User user = userRepository.findByEmail(email).get();
        Item newItem = Item.getInstance(itemDto.getName(),itemDto.getPrice(),
                itemDto.getQuantity(),itemDto.getItemStatus(),user);
        itemRepository.save(newItem);
        List<String> multi = new ArrayList<>(Arrays.asList("name-date.jpg","name.jpg","../pic","Y"));
        List<ItemImg> newItemImgList = temp(newItem, multi); //FileService - 멀티파일 리스트 받아서 List<ItemImg> 리턴
        itemImgRepository.saveAll(newItemImgList);
        return newItem;
    }
    //상품 조회
    public Item getItem(Long id) {
        return itemRepository.findById(id).get();
    }
    //상품목록 조회
    public List<Item> getItems() { return itemRepository.findAll();}
    //내 판매상품 조회
    public List<Item> getMyItems(String email) {
        User user = userRepository.findByEmail(email).get();
        return itemRepository.findByUser(user);
    }
    //상품 수량 차감 : 아이템수량 - 주문수량 비교
    public boolean checkAndReduce(long id, int quantity) {
        if (quantity > countItemQuantity(id)) return false;
        updateItemQuantity(id, quantity);
        return true;
    }
    //상품 수량 복구 : 주문취소시
    public void restore(long id, int quantity) {
        Item item = getItem(id);
        item.setQuantity(item.getQuantity()+quantity);
    }
    //상품 정보 업데이트
    public Item updateItem(ItemDto itemDto) {
        Item item = getItem(itemDto.getId());
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setItemStatus(itemDto.getItemStatus());
        return item;
    }
    //상품 삭제
    public void deleteItem(long id) {
        Item item = getItem(id);
        itemImgRepository.deleteAllByItem(item);
        cartItemRepository.deleteAllByItem(item);
        List<OrderItem> orderItemList = orderItemRepository.findByItem(item);
        for(OrderItem orderItem : orderItemList) {
            Order order = orderItem.getOrder();
            order.setPrice(order.getPrice()-orderItem.getPrice());
        }
        orderItemRepository.deleteAllByItem(item);
        itemRepository.deleteById(id);
    }
    //상품 quantity update
    private void updateItemQuantity(Long id, int quantity) {
        Item item = getItem(id);
        int remain = item.getQuantity() - quantity;
        item.setQuantity(remain);
        if(remain == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        else item.setItemStatus(ItemStatus.FOR_SALE);
    }
    //아이템 수량 확인 : 상품 조회 -> 상품 수량 확인
    private int countItemQuantity(Long id) {
        Item item = getItem(id);
        return item.getQuantity();
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
}
