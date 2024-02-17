package com.sws.rico.mapper;

import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
import com.sws.rico.entity.ItemImg;
import com.sws.rico.repository.CartItemRepository;
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class CartMapper {
    private final ItemImgRepository itemImgRepository;
    private final CartItemRepository cartItemRepository;

    private static CartItemDto getCartItemDto(CartItem cartItem, List<ItemImg> itemImg) {
        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(cartItem.getCount());
        cartItemDto.setPrice(cartItem.getItem().getPrice());
        cartItemDto.setItemId(cartItem.getItem().getId());
        cartItemDto.setItemName(cartItem.getItem().getName());
        cartItemDto.setItemImg(itemImg.stream().map(ItemMapper::toItemImgDto).collect(Collectors.toList()));
        return cartItemDto;
    }

    public CartDto toCartDto(Cart cart) {
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);

        CartDto cartDto = new CartDto();
        cartDto.setDate(cart.getCreatedAt());
        cartDto.setCartItemDto(cartItemList.stream()
                .map(cartItem -> getCartItemDto(cartItem, itemImgRepository.findAllByItem(cartItem.getItem())))
                .collect(Collectors.toList()));
        cartDto.setUser(cart.getUser().getEmail());
        return cartDto;
    }

    public CartItemDto tocartItemDto(CartItem cartItem,List<ItemImg> itemImg) {
        return getCartItemDto(cartItem, itemImg);
    }
}
