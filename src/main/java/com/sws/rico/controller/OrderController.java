package com.sws.rico.controller;

import com.sws.rico.dto.CartContainerDto;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.ConsumerService;
import com.sws.rico.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final ConsumerService consumerService;
    private final OrderService orderService;

    @ResponseBody
    @GetMapping ("/item/{itemId}/order")
    public OrderDto orderItem(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws CustomException {
        if(itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
        return consumerService.orderItem(itemId, quantity, authentication.getName());
    }

    @ResponseBody
    @PostMapping("/cart/order")
    public OrderDto orderCart(@RequestBody @Valid CartContainerDto cartContainerDto, Authentication authentication) throws CustomException {
        return consumerService.orderCarts(cartContainerDto.getCartDtoList(), authentication.getName());
    }

    @ResponseBody
    @GetMapping("/orders")
    public List<OrderDto> getOrders(Authentication authentication) {
        return orderService.getOrderDtoList(authentication.getName());
    }

    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws CustomException {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        consumerService.cancel(orderId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
