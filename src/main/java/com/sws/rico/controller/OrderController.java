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

//    @ResponseBody
//    @GetMapping ("/item/{itemId}/order/v2")
//    public ResponseEntity<OrderDto> orderItem(@PathVariable Long itemId, @RequestParam int quantity, Authentication authentication) throws CustomException {
//        if(itemId < 1 || quantity < 1) throw new IllegalArgumentException("Invalid Arguments");
//        OrderDto orderDto = orderService.orderItem(itemId, quantity, authentication.getName());
//        return ResponseEntity.ok(orderDto);
//    }

    @PostMapping ("/order/item")
    public ResponseEntity<OrderDto> orderItem(@RequestBody Map<String, String> order, Authentication authentication) throws CustomException {
        Long itemId; Integer count;
        try {
            itemId = Long.parseLong(order.get("item_id"));
            count = Integer.parseInt(order.get("count"));
        } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
            throw new IllegalArgumentException("invalid Arguments");
        }

        return ResponseEntity.ok(orderService.orderItem(itemId, count, authentication.getName()));
    }

//    @ResponseBody
//    @PostMapping("/cart/order/v2")
//    public ResponseEntity<OrderDto> orderCart_v2(@RequestBody List<Map<String, String>> orderIdList, Authentication authentication) throws CustomException {
//        long itemId = 0;
//        List<Long> orderList = new ArrayList<>();
//        if (orderIdList.size() != 0 ) {
//            try {
//                for (Map<String, String> m : orderIdList) {
//                    itemId = Long.parseLong(m.get("id"));
//                }
//            } catch (ClassCastException | NullPointerException | NumberFormatException exception) {
//                throw new IllegalArgumentException("Invalid Arguments");
//            }
//            if(itemId < 1 ) throw new IllegalArgumentException("Invalid Arguments");
//            orderList.add(itemId);
//        } else
//            throw new IllegalArgumentException("Invalid Arguments");
//        return ResponseEntity.ok(orderService.orderCart(orderList, authentication.getName()));
//    }

    @GetMapping("/order/cart")
    public ResponseEntity<OrderDto> orderCart(Authentication authentication) throws CustomException {
        return ResponseEntity.ok(orderService.orderCartv2(authentication.getName()));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderDto>> getOrders(Authentication authentication) throws CustomException {
        return ResponseEntity.ok(orderService.getOrderDtoPage(authentication.getName()));
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable Long orderId, Authentication authentication) throws CustomException {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        return ResponseEntity.ok(orderService.getOrderDto(orderId, authentication.getName()));
    }

    @GetMapping("/order/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, Authentication authentication) throws CustomException {
        if(orderId < 1) throw new IllegalArgumentException("Invalid Arguments");
        orderService.cancel(orderId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
