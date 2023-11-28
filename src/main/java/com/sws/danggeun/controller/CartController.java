package com.sws.danggeun.controller;

import com.sws.danggeun.dto.CartDto;
import com.sws.danggeun.dto.CartItemDto;
import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.exception.CartException;
import com.sws.danggeun.exception.CustomException;
import com.sws.danggeun.exception.ItemException;
import com.sws.danggeun.service.CartService;
import com.sws.danggeun.service.ConsumerService;
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
        return cartService.getCartDtoList(authentication.getName());
    }

    @ResponseBody
    @GetMapping("/cart/{cartId}")
    public CartDto getCart(@PathVariable Long cartId, Authentication authentication) throws CustomException {
        return cartService.getCartDto(cartId, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/cartItem/{cartItemId}")
    public CartItemDto getCartItem(@PathVariable Long cartItemId, Authentication authentication) throws CustomException {
        return cartService.getCartItemDto(cartItemId, authentication.getName());
    }

    @ResponseBody
    @PostMapping ("/cart/create")
    public CartDto createCart(Authentication authentication) {
        return cartService.getNewCart(authentication.getName());
    }

    @ResponseBody
    @PostMapping("/cart/{cartId}/add")
    public CartDto addItem(@PathVariable Long cartId, @RequestBody HashMap<String, String> item, Authentication authentication) throws CustomException {
        if(item.get("itemId").isEmpty() || item.get("quantity").isEmpty()) throw new IllegalArgumentException("Invalid Arguments");
        long itemId = Long.parseLong(item.get("itemId"));
        int quantity = Integer.parseInt(item.get("quantity"));
        if(itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
        return cartService.addItemToCart(itemId, quantity, cartId, authentication.getName());
    }

    @ResponseBody
    @PutMapping("/cartItem/{cartItemId}")
    public CartItemDto updateCartItem(@PathVariable Long cartItemId, @RequestParam int count, Authentication authentication) throws CustomException {
        return cartService.changeCartItemQuantity(cartItemId, count, authentication.getName());
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<?> deleteCart(@PathVariable Long cartId, Authentication authentication) throws CustomException {
        cartService.deleteCart(cartId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cartItem/{cartItemId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartItemId, Authentication authentication) throws CustomException {
        cartService.deleteCartItem(cartItemId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
