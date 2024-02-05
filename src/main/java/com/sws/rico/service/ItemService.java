package com.sws.rico.service;

import com.sws.rico.constant.CategoryDto;
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
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.hibernate.validator.internal.util.CollectionHelper.asSet;

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
    private final CategoryRepository categoryRepository;
    @Value("${img.location}") private String imgLocation;

    public ItemDto createItem(ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        User user = userRepository.findByEmail(email).get();
        Item newItem = Item.getInstance(itemDto, user);
        Item item = itemRepository.save(newItem);

        if(itemDto.getCategory().size() >0) {
            List<CategoryDto> categoryDto = itemDto.getCategory();
            for (CategoryDto category: categoryDto) {
                categoryRepository.save(CategoryWrapper.getInstance(category, item));
            }
        }

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        return getItemDto(item.getId());
    }

    public List<ItemDto> getItemBanner() {
        List<Item> itemList = itemRepository.findTop4ByOrderByCreatedAtDesc();
        return itemList.stream().map(item -> getItemDto(item.getId())).collect(toList());
    }

    public Page<ItemDto> getMyItemPage(String email, Pageable page) {
        User user = userRepository.findByEmail(email).get();
        return getItemDtoPage("", user.getName(),page);
    }

    public Page<ItemDto> getUserItemPage(Long userId, Pageable page) {
        User user = userRepository.findById(userId).get();
        return getItemDtoPage("", user.getName(),page);

    }

    public Page<ItemDto> getMainItemPage(String item, String user, Pageable page) {
        return getItemDtoPage(item,user,page);
    }

    public Page<ItemDto> getMainItemPagev2(String search, Pageable page) {
        return getItemDtoPagev2(search ,page);
    }

    public Page<ItemDto> getCategoryItem(CategoryDto category, Pageable page) {
        Page<Item> tmp = itemRepository.findByCategory(category, page);
        return tmp.map(pageItem -> ItemDto.getItemDto(pageItem, getCategorys(pageItem), getItemImgs(pageItem)));
    }

    public ItemDto getItemDto(Long itemId) {
        Item item = getItem(itemId);
        List<ItemImg> itemImgList = getItemImgs(item);
        List<CategoryWrapper> category = getCategorys(item);
        return ItemDto.getItemDto(item,category,itemImgList);
    }

    private List<CategoryWrapper> getCategorys(Item item) {
        return categoryRepository.findAllByItem(item);
    }

    public ItemDto updateItem(Long id, ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        if(!commonUserService.checkUser(id, email)) throw new ItemException("상품 수정 권한 없음");
        Item item = getItem(id);

        item.setName(itemDto.getName());
        item.setPrice(itemDto.getPrice());
        item.setQuantity(itemDto.getQuantity());
        item.setItemStatus(itemDto.getItemStatus());
        item.setBrief(itemDto.getBrief());
        item.setDescription(item.getDescription());

        if(itemDto.getCategory().size() >0) {
            List<CategoryDto> categoryDto = itemDto.getCategory();
            for (CategoryDto category: categoryDto) {
                categoryRepository.save(CategoryWrapper.getInstance(category, item));
            }
        }
        categoryRepository.deleteAllByItem(item);

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        itemImgRepository.deleteAllByItem(item);
        return getItemDto(item.getId());
    }

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
                .map(pageItem -> ItemDto.getItemDto(pageItem, getCategorys(pageItem), getItemImgs(pageItem)));
    }

    private Page<ItemDto> getItemDtoPagev2(String search, Pageable page) {
        return itemRepository.findPageItemv2(search, page)
                .map(pageItem -> ItemDto.getItemDto(pageItem,getCategorys(pageItem), getItemImgs(pageItem)));
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
        return itemImgRepository.findAllByItem(i);
    }

    public List<ItemDto> getLatestItem() {
        List<Item> itemList = itemRepository.findTop4ByOrderByCreatedAtDesc();
        return itemList.stream().map(item -> getItemDto(item.getId())).collect(toList());
    }


    public Map<String, Long> getCategory() {
        Map<String, Long> category = categoryRepository.findAll().stream().collect(groupingBy(res -> res.getCategory().toString(), counting()));
        Long total = category.values().stream().reduce(0L, Long::sum);
        category.put("total", total);
        return category;
    }
}
