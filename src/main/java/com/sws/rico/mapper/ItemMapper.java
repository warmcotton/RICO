package com.sws.rico.mapper;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.dto.ItemImgDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.repository.CategoryRepository;
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class ItemMapper {
    private final ItemImgRepository itemImgRepository;
    private final CategoryRepository categoryRepository;

    private static ItemDto getItemDto(Item item, List<CategoryWrapper> category, List<ItemImg> itemImgDtoList) {
        ItemDto itemDto = new ItemDto();
        List<CategoryDto> categoryDtos = new ArrayList<>();

        for(CategoryWrapper categoryWrapper : category) {
            categoryDtos.add(categoryWrapper.getCategory());
        }

        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setPrice(item.getPrice());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setItemStatus(item.getItemStatus());
        itemDto.setBrief(item.getBrief());
        itemDto.setDescription(item.getDescription());
        itemDto.setCategory(categoryDtos);
        itemDto.setItemImgDtoList(itemImgDtoList.stream().map(ItemMapper::toItemImgDto).collect(Collectors.toList()));
        itemDto.setUser(item.getUser().getName());
        return itemDto;
    }

    public static ItemImgDto toItemImgDto(ItemImg itemImg) {
        ItemImgDto itemImgDto = new ItemImgDto();
        itemImgDto.setId(itemImg.getId());
        itemImgDto.setImgName(itemImg.getImgName());
        itemImgDto.setImgUrl(itemImg.getImgUrl());
        itemImgDto.setRepimgYn(itemImg.getRepimgYn());
        return itemImgDto;
    }

    public ItemDto toItemDto(Item item) {
        List<ItemImg> itemImgList = itemImgRepository.findAllByItem(item);
        List<CategoryWrapper> category = categoryRepository.findAllByItem(item);
        return getItemDto(item,category,itemImgList);
    }
}