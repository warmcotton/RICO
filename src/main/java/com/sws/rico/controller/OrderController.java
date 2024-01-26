package com.sws.rico.controller;

import com.sws.rico.dto.CartContainerDto;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.ConsumerService;
import com.sws.rico.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final ConsumerService consumerService;
    private final OrderService orderService;


    @ResponseBody
    @GetMapping ("/item/{itemId}/order")
    public ResponseEntity<?> orderItem(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws CustomException {

        if(itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");

        consumerService.orderItem(itemId, quantity, authentication.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/orders"));

        return ResponseEntity.status(301).headers(headers).build();
    }


    @ResponseBody
    @PostMapping("/cart/order")
    public ResponseEntity<OrderDto> orderCart(@RequestBody @Valid CartContainerDto cartContainerDto, Authentication authentication) throws CustomException {

        return ResponseEntity.ok(consumerService.orderCarts(cartContainerDto.getCartDtoList(), authentication.getName()));
    }


    @ResponseBody
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getOrders(Authentication authentication, @PageableDefault(size = 10) Pageable page) {

        return ResponseEntity.ok(orderService.getOrderDtoList(authentication.getName(), page));
    }


    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws CustomException {

        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");

        consumerService.cancel(orderId, authentication.getName());

        return ResponseEntity.ok().build();
    }
}
