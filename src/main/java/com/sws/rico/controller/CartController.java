package com.sws.rico.controller;

import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

//    @ResponseBody
//    @GetMapping("/carts")
//    public ResponseEntity<Page<CartDto>> getCarts(Authentication authentication, @PageableDefault(size=5) Pageable page) {
//        return ResponseEntity.ok(cartService.getMyCartPage(authentication.getName(), page));
//    }

    @GetMapping("/cart")
    public ResponseEntity<CartDto> getCart(Authentication authentication) throws CustomException {
        return ResponseEntity.ok(cartService.getMyCart(authentication.getName()));
    }

//    @ResponseBody
//    @GetMapping("/cart/{cartId}")
//    public ResponseEntity<CartDto> getCart(@PathVariable Long cartId, Authentication authentication) throws CustomException {
//        if(cartId<1) throw new IllegalArgumentException("Invalid Arguments");
//        return ResponseEntity.ok(cartService.getCartDto(cartId, authentication.getName()));
//    }

//    @ResponseBody
//    @GetMapping("/cartItem/{cartItemId}")
//    public ResponseEntity<CartItemDto> getCartItem(@PathVariable Long cartItemId, Authentication authentication) throws CustomException {
//        if(cartItemId<1) throw new IllegalArgumentException("Invalid Arguments");
//        return ResponseEntity.ok(cartService.getCartItemDto(cartItemId, authentication.getName()));
//    }

    @PostMapping ("/create/cart")
    public ResponseEntity<CartDto> createCart(Authentication authentication) throws CustomException {
        return ResponseEntity.ok(cartService.createCart(authentication.getName()));
    }

//    @ResponseBody
//    @PostMapping("/cart/{cartId}/add")
//    public ResponseEntity<CartDto> addItem(@PathVariable Long cartId, @RequestBody HashMap<String, String> item, Authentication authentication) throws CustomException {
//        long itemId; int quantity;
//        try {
//            itemId = Long.parseLong(item.get("itemId"));
//            quantity = Integer.parseInt(item.get("quantity"));
//        } catch (ClassCastException exception) {
//            throw new IllegalArgumentException("Invalid Arguments");
//        }
//        if(cartId < 1|| itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
//        return ResponseEntity.ok(cartService.addItemToCart(itemId, quantity, cartId, authentication.getName()));
//    }

    @PostMapping("/cart")
    public ResponseEntity<CartDto> addItem(@RequestBody Map<String, String> item, Authentication authentication) throws CustomException {
        Long itemId; Integer count;
        try {
            itemId = Long.parseLong(item.get("item_id"));
            count = Integer.parseInt(item.get("count"));
        } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Arguments");
        }
        if(itemId < 1 || count < 1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(cartService.addItemToCart(itemId, count, authentication.getName()));
    }

//    @ResponseBody
//    @GetMapping("/cartItem/update/{cartItemId}")
//    public ResponseEntity<CartItemDto> updateCartItem(@PathVariable Long cartItemId, @RequestParam int count, Authentication authentication) throws CustomException {
//        if(cartItemId<1 || count<1) throw new IllegalArgumentException("Invalid Arguments");
//        return ResponseEntity.ok(cartService.changeCartItemQuantity(cartItemId, count, authentication.getName()));
//    }

    @PutMapping("/cart")
    public ResponseEntity<CartItemDto> updateCartItem(@RequestBody Map<String, String> update, Authentication authentication) throws CustomException {
        Long itemId; Integer count;
        try {
            itemId = Long.parseLong(update.get("item_id"));
            count = Integer.parseInt(update.get("count"));
        } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
            throw new IllegalArgumentException("Invalid Arguments");
        }
        if(itemId < 1 || count < 1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(cartService.changeCartItemQuantity(itemId, count, authentication.getName()));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<?> deleteCart(Authentication authentication) throws CustomException {
        cartService.deleteCart(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartItem/{itemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long itemId, Authentication authentication) throws CustomException {
        if(itemId < 1 ) throw new IllegalArgumentException("Invalid Arguments");
        cartService.deleteCartItem(itemId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
