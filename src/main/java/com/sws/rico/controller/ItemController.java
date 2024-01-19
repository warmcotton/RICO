package com.sws.rico.controller;

import com.sws.rico.dto.ItemDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.ConsumerService;
import com.sws.rico.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ConsumerService consumerService;
    private final ItemService itemService;

    @ResponseBody
    @GetMapping("/items")
    public List<ItemDto> getItems(@RequestParam(value = "item", defaultValue = "") String item) {
        return itemService.getItemDtoList(item);
    }

    @ResponseBody
    @GetMapping("/item/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return itemService.getItemDto(itemId);
    }

    @ResponseBody
    @PostMapping("/item")
    public ItemDto saveItem(@RequestPart @Valid ItemDto itemDto, @RequestPart List<MultipartFile> itemFileList, Authentication authentication) throws CustomException {
        return itemService.saveItem(itemDto, itemFileList, authentication.getName());
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId, Authentication authentication) throws CustomException {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        itemService.deleteItem(itemId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PutMapping("/item/{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId, @RequestPart @Valid ItemDto itemDto,
                              @RequestPart List<MultipartFile> itemFileList, Authentication authentication) throws CustomException, IOException {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return itemService.updateItem(itemId, itemDto, itemFileList, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/user/{userId}/items")
    public List<ItemDto> getMyItems(@PathVariable Long userId, Authentication authentication) throws CustomException {
        if(userId<1) throw new IllegalArgumentException("Invalid Arguments");
        return itemService.getMyItems(userId, authentication.getName());
    }
}