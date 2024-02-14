package com.sws.rico.service;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.constant.Role;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static java.util.stream.Collectors.*;

@Service
@Transactional(rollbackOn = {CustomException.class})
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final DeletedItemRepository deletedItemRepository;
    private final CommonUserService commonUserService;
    private final CommonItemService commonItemService;
    private final ItemImgService itemImgService;
    private final CategoryRepository categoryRepository;
    @Value("${img.location}") private String imgLocation;

    public ItemDto createItem(ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Item newItem = Item.getInstance(itemDto, user);
        Item item = itemRepository.save(newItem);

        if(itemDto.getCategory().size() >0) {
            List<CategoryDto> categoryDto = itemDto.getCategory();
            for (CategoryDto category: categoryDto) {
                categoryRepository.save(CategoryWrapper.getInstance(category, item));
            }
        }

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = itemImgService.saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        return commonItemService.getItemDto(item.getId());
    }

    public List<ItemDto> getLatestItem() throws CustomException {
        List<Item> itemList = itemRepository.findTop8ByOrderByCreatedAtDesc();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(commonItemService.getItemDto(item.getId()));
        }
        return itemDtos;
    }

    public List<ItemDto> getPopularItem() throws CustomException {
        Page<Item> itemList = itemRepository.findPopularItem(PageRequest.of(0, 8));
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList.getContent()) {
            itemDtos.add(commonItemService.getItemDto(item.getId()));
        }
        return itemDtos;
    }

    public List<ItemDto> getItemBanner() throws CustomException {
        List<Item> itemList = itemRepository.findTop4ByOrderByCreatedAtDesc();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(commonItemService.getItemDto(item.getId()));
        }
        return itemDtos;
    }

//    public Page<ItemDto> getMainItemPage(String item, String user, Pageable page) {
//        return commonItemService.getItemDtoPageByEmail("","",page);
//    }

    public Page<ItemDto> getMyItemPage(String email, Pageable page) throws UserException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        return commonItemService.getItemDtoPageByEmail("", user.getEmail(),page);
    }

    public Page<ItemDto> getUserItemPage(Long userId, Pageable page) throws CustomException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        if (user.getRole()!= Role.SUPPLIER) throw new ItemException("판매자가 아닙니다.");
        return commonItemService.getItemDtoPageByEmail("", user.getEmail(),page);
    }

    public Page<ItemDto> getMainItemPage(String search, Pageable page) {
        return commonItemService.getItemDtoPage(search ,page);
    }

    public Page<ItemDto> getCategoryItem(CategoryDto category, Pageable page) {
        Page<Item> tmp = itemRepository.findByCategory(category, page);
        return tmp.map(pageItem -> ItemDto.getItemDto(pageItem, categoryRepository.findAllByItem(pageItem), itemImgRepository.findAllByItem(pageItem)));
    }

    public Map<String, Long> getCategory() {
        Map<String, Long> category = categoryRepository.findAll().stream().collect(groupingBy(res -> res.getCategory().toString(), counting()));
        Long total = category.values().stream().reduce(0L, Long::sum);
        category.put("total", total);
        return category;
    }

    public ItemDto updateItem(Long id, ItemDto itemDto, List<MultipartFile> imgList, String email) throws CustomException {
        if(!commonUserService.validateUserItem(id, email)) throw new ItemException("접근 권한이 없습니다.");

        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));

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
            List<ItemImg> newItemImgList = itemImgService.saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        itemImgRepository.deleteAllByItem(item);
        return commonItemService.getItemDto(item.getId());
    }

    public void deleteItem(long id, String email) throws CustomException {
        if(!commonUserService.validateUserItem(id, email)) throw new ItemException("접근 권한이 없습니다.");
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        DeletedItem deletedItem = DeletedItem.getInstance(item);
        deletedItemRepository.save(deletedItem);
        itemImgRepository.deleteAllByItem(item);
        cartItemRepository.deleteAllByItem(item);
        categoryRepository.deleteAllByItem(item);
        List<OrderItem> orderItemList = orderItemRepository.findByItem(item);

        for(OrderItem orderItem : orderItemList) {
            orderItem.setItem(null);
            orderItem.setDeletedItem(deletedItem);
        }
        itemRepository.deleteById(id);
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id).get();
    }

    private List<Item> getItems() { return itemRepository.findAll();}

    private List<ItemImg> getItemImgs(Item i) {
        return itemImgRepository.findAllByItem(i);
    }

//    public Page<ItemDto> getItemDtoPagev2(String search, Pageable page) {
//        return itemRepository.findPageItemv2(search, page)
//                .map(pageItem -> ItemDto.getItemDto(pageItem,categoryRepository.findAllByItem(pageItem), getItemImgs(pageItem)));
//    }

//    private Page<ItemDto> getItemDtoPage(String item, String user, Pageable page) {
//        return itemRepository.findPageItem(item, user, page)
//                .map(pageItem -> ItemDto.getItemDto(pageItem, categoryRepository.findAllByItem(pageItem), getItemImgs(pageItem)));
//    }
}
