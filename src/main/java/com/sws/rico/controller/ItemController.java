package com.sws.rico.controller;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/category")
    public ResponseEntity<Map<String, Long>> getCategory() {
        return ResponseEntity.ok(itemService.getCategory());
    }

    @GetMapping("/items")
    public ResponseEntity<Page<ItemDto>> getItems(@RequestParam(value = "search", defaultValue = "") String search,
                                                  @RequestParam(value = "category", defaultValue = "") CategoryDto category,
                                                  @PageableDefault(size=6, sort = "createdAt", direction = Sort.Direction.DESC)
                                                  Pageable page) {
        return ResponseEntity.ok(itemService.getMainItemPage(search, category, page));
    }

    @GetMapping("/items/banner")
    public ResponseEntity<List<ItemDto>> getBanner() {
        return ResponseEntity.ok(itemService.getItemBanner());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.getItem(itemId));
    }

    @PostMapping("/item")
    public ResponseEntity<ItemDto> saveItem(@RequestPart List<MultipartFile> itemFileList, @RequestPart @Valid ItemDto itemDto, Authentication authentication) {
        if(itemFileList.size() < 1 || itemFileList.size() > 6) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.createItem(itemDto, itemFileList, authentication.getName()));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId, Authentication authentication) {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        itemService.deleteItem(itemId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId, @RequestPart @Valid ItemDto itemDto,
                              @RequestPart List<MultipartFile> itemFileList, Authentication authentication) {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.updateItem(itemId, itemDto, itemFileList, authentication.getName()));
    }

    @GetMapping("/user/myitems")
    public ResponseEntity<Page<ItemDto>> getMyItems(Authentication authentication, @PageableDefault(size=10) Pageable page) {
        return ResponseEntity.ok(itemService.getMyItemPage(authentication.getName(), page));
    }

    @GetMapping("/user/{userId}/items")
    public ResponseEntity<Page<ItemDto>> getUserItems(@PathVariable Long userId, @PageableDefault(size=10) Pageable page) {
        if(userId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.getUserItemPage(userId, page));
    }
}
