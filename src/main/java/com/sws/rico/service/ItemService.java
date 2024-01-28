package com.sws.rico.service;

import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.FileException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeletedItemRepository deletedItemRepository;
    private final CommonUserService commonUserService;
    @Value("${img.location}") private String imgLocation;

    //상품생성(판매)
    public ItemDto createItem(ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        User user = userRepository.findByEmail(email).get();
        Item newItem = Item.getInstance(itemDto.getName(),itemDto.getPrice(), itemDto.getQuantity(),itemDto.getItemStatus(), user);
        Item item = itemRepository.save(newItem);

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        return getItemDto(item.getId());
    }

    //내 판매상품 조회
    public Page<ItemDto> getMyItemPage(String email, Pageable page) {
        User user = userRepository.findByEmail(email).get();
        return getItemDtoPage("", user.getName(),page);
    }

    //유저 판매상품 조회
    public Page<ItemDto> getUserItemPage(Long userId, Pageable page) {
        User user = userRepository.findById(userId).get();
        return getItemDtoPage("", user.getName(),page);

    }

    public Page<ItemDto> getMainItemPage(String item, String user, Pageable page) {
        return getItemDtoPage(item,user,page);
    }

    public ItemDto getItemDto(Long itemId) {
        Item item = getItem(itemId);
        List<ItemImg> itemImgList = getItemImgs(item);
        return ItemDto.getItemDto(item,itemImgList);
    }

    //상품 업데이트
    public ItemDto updateItem(Long id, ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        if(!commonUserService.checkUser(id, email)) throw new ItemException("상품 수정 권한 없음");
        Item item = getItem(id);
        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setItemStatus(itemDto.getItemStatus());

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        itemImgRepository.deleteAllByItem(item);
        return getItemDto(item.getId());
    }

    //상품 삭제
    public void deleteItem(long id, String email) throws CustomException {
        if(!commonUserService.checkUser(id, email)) throw new ItemException("상품 삭제 권한 없음");
        Item item = getItem(id);
        DeletedItem deletedItem = DeletedItem.getInstance(item);
        deletedItemRepository.save(deletedItem);
        itemImgRepository.deleteAllByItem(item);
        cartItemRepository.deleteAllByItem(item);
        List<OrderItem> orderItemList = orderItemRepository.findByItem(item);

        for(OrderItem orderItem : orderItemList) {
            orderItem.setItem(null);
            orderItem.setDeletedItem(deletedItem);
        }
        itemRepository.deleteById(id);
    }

    private Page<ItemDto> getItemDtoPage(String item, String user, Pageable page) {
        return itemRepository.findPageItem(item, user, page)
                .map(pageItem -> ItemDto.getItemDto(pageItem, getItemImgs(pageItem)));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id).get();
    }

    private List<Item> getItems() { return itemRepository.findAll();}

    private List<ItemImg> saveImage(Item item, List<MultipartFile> multi) throws CustomException {
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

    private ItemImg uploadImage(Item item, MultipartFile file, String repImgYn) throws CustomException {
        UUID uuid = UUID.randomUUID();
        String oriImgName = file.getOriginalFilename();
        assert oriImgName != null;
        String extension = oriImgName.substring(oriImgName.lastIndexOf("."));
        String savedFileName = uuid + extension;
        String imgUrl = "/images/rico/"+savedFileName;
        try {
            FileOutputStream fos = new FileOutputStream(imgLocation+"/"+savedFileName);
            fos.write(file.getBytes());
            fos.close();
        } catch (IOException exception) {
            throw new FileException("file save failed");
        }
        return ItemImg.getInstance(savedFileName, oriImgName, imgUrl, repImgYn, item);
    }

    private List<ItemImg> getItemImgs(Item i) {
        return itemImgRepository.findByItem(i);
    }
}
