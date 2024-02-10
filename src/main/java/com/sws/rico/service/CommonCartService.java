package com.sws.rico.service;

import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.CustomException;
import com.sws.rico.repository.CartItemRepository;
import com.sws.rico.repository.CartRepository;
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = {CustomException.class})
public class CommonCartService {
    private final ItemImgRepository itemImgRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    protected CartDto getCartDto(Cart c, List<CartItem> cartItemList) {
        CartDto cartDto = new CartDto();
        cartDto.setDate(c.getCreatedAt());
        cartDto.setCartItemDto(cartItemList.stream()
                .map(cartItem -> CartItemDto.getInstance(cartItem, itemImgRepository.findAllByItem(cartItem.getItem())))
                .collect(Collectors.toList()));
        cartDto.setUser(c.getUser().getEmail());
        return cartDto;
    }

    protected List<CartItem> getCartItemsByCartId(Long cartId) throws CustomException {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new CartException("장바구니 정보가 없습니다."));
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);
        return cartItemList;
    }
}
