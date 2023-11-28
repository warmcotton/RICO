package com.sws.danggeun.controller;

import com.sws.danggeun.dto.CartDto;
import com.sws.danggeun.dto.OrderDto;
import com.sws.danggeun.exception.CustomException;
import com.sws.danggeun.service.ConsumerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final ConsumerService consumerService;

    @ResponseBody
    @GetMapping ("/item/{itemId}/order")
    public OrderDto orderItem(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws CustomException {
        return consumerService.orderSingleItem(itemId, quantity, authentication.getName());
    }
    @ResponseBody
    @PostMapping("/cart/order")
    public OrderDto orderCart(@RequestBody List<CartDto> cartDtoList, Authentication authentication) throws CustomException {
        return consumerService.orderCarts(cartDtoList, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/orders")
    public List<OrderDto> getOrders(Authentication authentication) {
        return consumerService.getOrderDtoList(authentication.getName());
    }

    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws CustomException {
        consumerService.cancel(orderId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
