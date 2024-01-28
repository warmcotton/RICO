package com.sws.rico.controller;

import com.sws.rico.dto.CartContainerDto;
import com.sws.rico.dto.OrderDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.UserException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final ConsumerService consumerService;
    private final OrderService orderService;


    @ResponseBody
    @GetMapping ("/item/{itemId}/order/v2")
    public ResponseEntity<OrderDto> orderItem(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws CustomException {
        if(itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
        OrderDto orderDto = orderService.orderItem(itemId, quantity, authentication.getName());
        return ResponseEntity.ok(orderDto);
    }

//    @ResponseBody
//    @PostMapping("/cart/order")
//    public ResponseEntity<OrderDto> orderCart(@RequestBody @Valid CartContainerDto cartContainerDto, Authentication authentication) throws CustomException {
//
//        return ResponseEntity.ok(orderService.orderCart(cartContainerDto.getCartDtoList(), authentication.getName()));
//    }

    @ResponseBody
    @PostMapping("/cart/order/v2")
    public ResponseEntity<OrderDto> orderCart_v2(@RequestBody List<Map<String, String>> orderIdList, Authentication authentication) throws CustomException {
        long itemId = 0;
        List<Long> orderList = new ArrayList<>();
        if (orderIdList.size() != 0 ) {
            try {
                for (Map<String, String> m : orderIdList) {
                    itemId = Long.parseLong(m.get("id"));
                }
            } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
                throw new IllegalArgumentException("Invalid Arguments");
            }
            if(itemId < 1 ) throw new IllegalArgumentException("Invalid Arguments");
            orderList.add(itemId);
        } else
            throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(orderService.orderCart(orderList, authentication.getName()));
    }


    @ResponseBody
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderDto>> getOrders(Authentication authentication, @PageableDefault(size = 10) Pageable page) {
        return ResponseEntity.ok(orderService.getOrderDtoList(authentication.getName(), page));
    }


//    @GetMapping("/order/{orderId}/cancel")
//    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws CustomException {
//        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
//        consumerService.cancel(orderId, authentication.getName());
//        return ResponseEntity.ok().build();
//    }


    @GetMapping("/order/{orderId}/cancel/v2")
    public ResponseEntity<?> cancel_v2(@PathVariable Long orderId, Authentication authentication) throws CustomException {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        orderService.cancel(orderId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
