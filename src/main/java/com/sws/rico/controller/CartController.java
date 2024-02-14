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

    @GetMapping("/cart")
    public ResponseEntity<CartDto> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getMyCart(authentication.getName()));
    }

    @PostMapping ("/create/cart")
    public ResponseEntity<CartDto> createCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.createCart(authentication.getName()));
    }

    @PostMapping("/cart")
    public ResponseEntity<CartDto> addItem(@RequestBody Map<String, String> item, Authentication authentication) {
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

    @PutMapping("/cart")
    public ResponseEntity<CartItemDto> updateCartItem(@RequestBody Map<String, String> update, Authentication authentication) {
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
    public ResponseEntity<?> deleteCart(Authentication authentication) {
        cartService.deleteCart(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartItem/{itemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long itemId, Authentication authentication) {
        if(itemId < 1 ) throw new IllegalArgumentException("Invalid Arguments");
        cartService.deleteCartItem(itemId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
