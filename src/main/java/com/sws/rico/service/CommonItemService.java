package com.sws.rico.service;

import com.sws.rico.dto.ItemDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.CategoryRepository;
import com.sws.rico.repository.ItemImgRepository;
import com.sws.rico.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonItemService {
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final CategoryRepository categoryRepository;

    public ItemDto getItemDto(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        List<ItemImg> itemImgList = itemImgRepository.findAllByItem(item);
        List<CategoryWrapper> category = categoryRepository.findAllByItem(item);;
        return ItemDto.getItemDto(item,category,itemImgList);
    }

    protected Page<ItemDto> getItemDtoPageByEmail(String item, String email, Pageable page) {
        return itemRepository.findUserItem(item, email, page)
                .map(pageItem -> ItemDto.getItemDto(pageItem, categoryRepository.findAllByItem(pageItem), itemImgRepository.findAllByItem(pageItem)));
    }
}
