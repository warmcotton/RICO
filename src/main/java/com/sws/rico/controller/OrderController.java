package com.sws.rico.controller;

import com.sws.rico.dto.OrderDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping ("/order/item")
    public ResponseEntity<OrderDto> orderItem(@RequestBody Map<String, String> order, Authentication authentication) {
        Long itemId; Integer count;
        try {
            itemId = Long.parseLong(order.get("item_id"));
            count = Integer.parseInt(order.get("count"));
        } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
            throw new IllegalArgumentException("invalid Arguments");
        }

        return ResponseEntity.ok(orderService.orderItem(itemId, count, authentication.getName()));
    }

    @GetMapping("/order/cart")
    public ResponseEntity<OrderDto> orderCart(Authentication authentication) {
        return ResponseEntity.ok(orderService.orderCartv2(authentication.getName()));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderDtoPage(authentication.getName()));
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId, Authentication authentication) {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(orderService.getOrderDto(orderId, authentication.getName()));
    }

    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        orderService.cancel(orderId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
