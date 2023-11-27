package com.sws.danggeun.controller;

import com.sws.danggeun.dto.ItemDto;
import com.sws.danggeun.service.ConsumerService;
import com.sws.danggeun.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ConsumerService consumerService;
    private final ItemService itemService;

    @ResponseBody
    @GetMapping("/items")
    public List<ItemDto> getItems() {
        return consumerService.viewItemList();
    }

    @ResponseBody
    @GetMapping("/item/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.getItemDto(itemId);
    }

    @ResponseBody
    @PostMapping("/item")
    public ItemDto saveItem(@RequestPart ItemDto itemDto, @RequestPart List<MultipartFile> itemFileList, Authentication authentication) throws IOException {
        return itemService.saveItem(itemDto, itemFileList, authentication.getName());
    }
}
