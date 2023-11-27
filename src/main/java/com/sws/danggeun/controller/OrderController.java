package com.sws.danggeun.controller;

import com.sws.danggeun.dto.OrderDto;
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
    @GetMapping ("/order/{itemId}")
    public OrderDto buy(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws Exception {
        return consumerService.buySingleItem(itemId, quantity, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/orders")
    public List<OrderDto> orders(Authentication authentication) {
        return consumerService.viewOrderList(authentication.getName());
    }

    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws Exception {
        consumerService.cancel(orderId);
        return ResponseEntity.ok().build();
    }
}
