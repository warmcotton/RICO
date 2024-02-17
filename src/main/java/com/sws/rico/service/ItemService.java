package com.sws.rico.service;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.constant.Role;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.UserException;
import com.sws.rico.mapper.ItemMapper;
import com.sws.rico.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static java.util.stream.Collectors.*;

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
    private final ItemMapper itemMapper;
    private final ItemImgService itemImgService;
    private final CategoryRepository categoryRepository;

    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        return itemMapper.toItemDto(item);
    }

    public ItemDto createItem(ItemDto itemDto, List<MultipartFile> imgList, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Item newItem = Item.createItem(itemDto, user);
        Item item = itemRepository.save(newItem);

        if(itemDto.getCategory().size() >0) {
            List<CategoryDto> categoryDto = itemDto.getCategory();
            for (CategoryDto category: categoryDto) {
                categoryRepository.save(CategoryWrapper.createCategoryWrapper(category, item));
            }
        }

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = itemImgService.saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        return itemMapper.toItemDto(item);
    }

    public List<ItemDto> getItemBanner() {
        List<Item> itemList = itemRepository.findTop4ByOrderByCreatedAtDesc();
        List<ItemDto> itemDtos = new ArrayList<>();
        for (Item item : itemList) {
            itemDtos.add(itemMapper.toItemDto(item));
        }
        return itemDtos;
    }

    public Page<ItemDto> getMyItemPage(String email, Pageable page) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        return getItemDtoPageByEmail("", user.getEmail(),page);
    }

    public Page<ItemDto> getUserItemPage(Long userId, Pageable page) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        if (user.getRole()!= Role.SUPPLIER) throw new ItemException("판매자가 아닙니다.");
        return getItemDtoPageByEmail("", user.getEmail(),page);
    }

    public Page<ItemDto> getMainItemPage(String search, CategoryDto category, Pageable page) {
        if (category==null) return itemRepository.findPageItem(search, page).map(itemMapper::toItemDto);
        else return itemRepository.findByCategory(category, page).map(itemMapper::toItemDto);
    }

    private Page<ItemDto> getItemDtoPageByEmail(String item, String email, Pageable page) {
        return itemRepository.findUserItem(item, email, page).map(itemMapper::toItemDto);
    }

    public Map<String, Long> getCategory() {
        Map<String, Long> category = categoryRepository.findAll().stream().collect(groupingBy(res -> res.getCategory().toString(), counting()));
        Long total = category.values().stream().reduce(0L, Long::sum);
        category.put("total", total);
        return category;
    }

    public ItemDto updateItem(Long id, ItemDto itemDto, List<MultipartFile> imgList, String email) {
        if(!validateUserItem(id, email)) throw new ItemException("접근 권한이 없습니다.");

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
                categoryRepository.save(CategoryWrapper.createCategoryWrapper(category, item));
            }
        }
        categoryRepository.deleteAllByItem(item);

        if(imgList.size() > 0) {
            List<ItemImg> newItemImgList = itemImgService.saveImage(item, imgList);
            itemImgRepository.saveAll(newItemImgList);
        }
        itemImgRepository.deleteAllByItem(item);
        return itemMapper.toItemDto(item);
    }

    public void deleteItem(long id, String email) {
        if(!validateUserItem(id, email)) throw new ItemException("접근 권한이 없습니다.");
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        DeletedItem deletedItem = DeletedItem.createDeletedItem(item);
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

    protected boolean validateUserItem(Long itemId, String email) {
        Item item = itemRepository.findById(itemId).get();
        return email.equals(item.getUser().getEmail());
    }
}
