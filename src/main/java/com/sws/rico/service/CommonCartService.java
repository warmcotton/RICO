package com.sws.rico.service;

import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonCartService {
    private final ItemImgRepository itemImgRepository;

    protected CartDto getCartDto(Cart c, List<CartItem> cartItemList) {
        CartDto cartDto = new CartDto();
        cartDto.setDate(c.getCreatedAt());
        cartDto.setCartItemDto(cartItemList.stream()
                .map(cartItem -> CartItemDto.getInstance(cartItem, itemImgRepository.findAllByItem(cartItem.getItem())))
                .collect(Collectors.toList()));
        cartDto.setUser(c.getUser().getEmail());
        return cartDto;
    }
}
