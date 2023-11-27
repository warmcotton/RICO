package com.sws.danggeun.service;

import com.sws.danggeun.constant.ItemStatus;
import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.entity.*;
import com.sws.danggeun.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository; //controller레이어로 이동 고려 -> createItem 에서 파라미터 email 대신 User
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    @Value("${img.location}") private String imgLocation;
    //상품생성(판매)
    public ItemDto saveItem(ItemDto itemDto, List<MultipartFile> imgList, String email) throws IOException { //controller에서 dto 검사,
        User user = userRepository.findByEmail(email).get();
        Item newItem = Item.getInstance(itemDto.getName(),itemDto.getPrice(), itemDto.getQuantity(),itemDto.getItemStatus(), user);
        Item item = itemRepository.save(newItem);
        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = saveImage(item, imgList); //FileService - 멀티파일 리스트 받아서 List<ItemImg> 리턴
            itemImgRepository.saveAll(newItemImgList);
        }
        return getItemDto(item.getId());
    }
    //상품 조회
    private Item getItem(Long id) {
        return itemRepository.findById(id).get();
    }
    //상품목록 조회
    protected List<Item> getItems() { return itemRepository.findAll();}
    //내 판매상품 조회
    public List<ItemDto> getMyItems(Long userId, String email) throws Exception {
        if(!checkUser(userId, email)) throw new Exception("상품 접근 권한 없음");
        User user = userRepository.findByEmail(email).get();
        return itemRepository.findByUser(user).stream()
                .map(item -> getItemDto(item.getId())).collect(Collectors.toList());
    }
    public ItemDto getItemDto(Long id) {
        Item item = getItem(id);
        List<ItemImg> itemImgList = getItemImgs(item);
        return ItemDto.getDto(item,itemImgList);
    }
    public List<ItemDto> searchItemDto(String name) {
        return itemRepository.findByNameContaining(name).stream()
                .map(item -> getItemDto(item.getId())).collect(Collectors.toList());
    }
    //상품 수량 차감 : 아이템수량 - 주문수량 비교
    protected boolean checkAndReduce(long id, int quantity) {
        if (quantity > countItemQuantity(id)) return false;
        updateItemQuantity(id, quantity);
        return true;
    }
    //상품 수량 복구 : 주문취소시
    protected void restore(long id, int quantity) {
        Item item = getItem(id);
        item.setQuantity(item.getQuantity()+quantity);
    }
    //상품 정보 업데이트
    public ItemDto updateItem(Long id, ItemDto itemDto, List<MultipartFile> imgList, String email) throws Exception {
        if(!checkUser(id, email)) throw new Exception("상품 수정 권한 없음");
        Item item = getItem(id);
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setItemStatus(itemDto.getItemStatus());
        return getItemDto(item.getId());
    }
    //상품 삭제
    public void deleteItem(long id, String email) throws Exception {
        if(!checkUser(id, email)) throw new Exception("상품 삭제 권한 없음");
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

    private List<ItemImg> saveImage(Item item, List<MultipartFile> multi) throws IOException { // >0
        List<ItemImg> itemImgList = new ArrayList<>();
        int count = 0;
        String repImgYn ="Y";
        for (MultipartFile file : multi) {
            if (count != 0) repImgYn="N";
            ItemImg itemImg = uploadImage(item,file,repImgYn);
            itemImgList.add(itemImg);
            count++;
        }
        return itemImgList;
    }

    private ItemImg uploadImage(Item item, MultipartFile file, String repImgYn) throws IOException {
        UUID uuid = UUID.randomUUID();
        String oriImgName = file.getOriginalFilename();
        assert oriImgName != null;
        String extension = oriImgName.substring(oriImgName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String imgUrl = "/images/dang-geun/"+savedFileName;
        FileOutputStream fos = new FileOutputStream(imgLocation+"/"+savedFileName);
        fos.write(file.getBytes());
        fos.close();
        return ItemImg.getInstance(savedFileName, oriImgName, imgUrl, repImgYn, item);
    }

    public List<ItemImg> getItemImgs(Item i) {
        return itemImgRepository.findByItem(i);
    }

    private boolean checkUser(Long id, String email) {
        return email.equals(getItem(id).getUser().getEmail());
    }
}
