package com.sws.rico.controller;

import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @ResponseBody
    @GetMapping("/carts")
    public List<CartDto> getCarts(Authentication authentication) {
        return cartService.getCartDtosByEmail(authentication.getName());
    }

    @ResponseBody
    @GetMapping("/cart/{cartId}")
    public CartDto getCart(@PathVariable Long cartId, Authentication authentication) throws CustomException {
        if(cartId<1) throw new IllegalArgumentException("Invalid Arguments");
        return cartService.getCartDto(cartId, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/cartItem/{cartItemId}")
    public CartItemDto getCartItem(@PathVariable Long cartItemId, Authentication authentication) throws CustomException {
        if(cartItemId<1) throw new IllegalArgumentException("Invalid Arguments");
        return cartService.getCartItemDto(cartItemId, authentication.getName());
    }

    @ResponseBody
    @PostMapping ("/cart/create")
    public CartDto createCart(Authentication authentication) {
        return cartService.createCart(authentication.getName());
    }

    @ResponseBody
    @PostMapping("/cart/{cartId}/add")
    public CartDto addItem(@PathVariable Long cartId, @RequestBody HashMap<String, String> item, Authentication authentication) throws CustomException {
        long itemId; int quantity;
        try {
            itemId = Long.parseLong(item.get("itemId"));
            quantity = Integer.parseInt(item.get("quantity"));
        } catch (ClassCastException exception) {
            throw new IllegalArgumentException("Invalid Arguments");
        }
        if(cartId < 1|| itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
        return cartService.addItemToCart(itemId, quantity, cartId, authentication.getName());
    }

    @ResponseBody
    @PutMapping("/cartItem/{cartItemId}")
    public CartItemDto updateCartItem(@PathVariable Long cartItemId, @RequestParam int count, Authentication authentication) throws CustomException {
        if(cartItemId<1 || count<1) throw new IllegalArgumentException("Invalid Arguments");
        return cartService.changeCartItemQuantity(cartItemId, count, authentication.getName());
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cartId, Authentication authentication) throws CustomException {
        if(cartId<1) throw new IllegalArgumentException("Invalid Arguments");
        cartService.deleteCart(cartId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartItem/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId, Authentication authentication) throws CustomException {
        if(cartItemId<1) throw new IllegalArgumentException("Invalid Arguments");
        cartService.deleteCartItem(cartItemId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
