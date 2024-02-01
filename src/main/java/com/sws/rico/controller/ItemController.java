package com.sws.rico.controller;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.dto.ItemDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemRepository itemRepository;

    @ResponseBody
    @GetMapping("/items")
    public ResponseEntity<Page<ItemDto>> getItems(@RequestParam(value = "item", defaultValue = "") String item,
                                                  @RequestParam(value = "user", defaultValue = "") String user,
                                                  @PageableDefault(size=10) Pageable page) {
        return ResponseEntity.ok(itemService.getMainItemPage(item, user, page));
    }

    @ResponseBody
    @GetMapping("/itemsv2")
    public ResponseEntity<Page<ItemDto>> getItems(@RequestParam(value = "search", defaultValue = "") String search,
                                                  @PageableDefault(size=6, sort = "createdAt", direction = Sort.Direction.DESC)
                                                  Pageable page) {
        return ResponseEntity.ok(itemService.getMainItemPagev2(search, page));
    }

    @ResponseBody
    @GetMapping("/items/category/{category}")
    public ResponseEntity<Page<ItemDto>> getItemsByCategory(@PathVariable CategoryDto category,
                                                  @PageableDefault(size=6, sort = "createdAt", direction = Sort.Direction.DESC) Pageable page) {
        return ResponseEntity.ok(itemService.getCategoryItem(category, page));
    }


    @ResponseBody
    @GetMapping("/item/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable Long itemId) {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.getItemDto(itemId));
    }

    @ResponseBody
    @PostMapping("/item")
    public ResponseEntity<ItemDto> saveItem(@RequestPart List<MultipartFile> itemFileList, @RequestPart @Valid ItemDto itemDto, Authentication authentication) throws CustomException {
        return ResponseEntity.ok(itemService.createItem(itemDto, itemFileList, authentication.getName()));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId, Authentication authentication) throws CustomException {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        itemService.deleteItem(itemId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PutMapping("/item/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long itemId, @RequestPart @Valid ItemDto itemDto,
                              @RequestPart List<MultipartFile> itemFileList, Authentication authentication) throws CustomException {
        if(itemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.updateItem(itemId, itemDto, itemFileList, authentication.getName()));
    }

    @ResponseBody
    @GetMapping("/user/myitems")
    public ResponseEntity<Page<ItemDto>> getMyItems(Authentication authentication, @PageableDefault(size=10) Pageable page) {
        return ResponseEntity.ok(itemService.getMyItemPage(authentication.getName(), page));
    }

    @ResponseBody
    @GetMapping("/user/{userId}/items")
    public ResponseEntity<Page<ItemDto>> getUserItems(@PathVariable Long userId, @PageableDefault(size=10) Pageable page) {
        if(userId<1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(itemService.getUserItemPage(userId, page));
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("bannerItemList", itemService.getItemBanner());
        model.addAttribute("latestItemList", itemService.getLatestItem());
        return "index";
    }
}
