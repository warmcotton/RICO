package com.sws.danggeun.dto;

import com.sws.danggeun.entity.Cart;
import com.sws.danggeun.entity.CartItem;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class CartDto {
    @NotNull
    private Long id;
    private LocalDateTime date;
    @Size(min = 1)
    private List<@Valid CartItemDto> cartItemDto;
    private String user;
    public static CartDto getInstance(Cart c, List<CartItem> cartItemList) {
        CartDto cartDto = new CartDto();
        cartDto.setId(c.getId());
        cartDto.setDate(c.getCreatedAt());
        cartDto.setCartItemDto(cartItemList.stream().map(CartItemDto::getInstance).collect(Collectors.toList()));
        cartDto.setUser(c.getUser().getEmail());
        return cartDto;
    }

    @Override
    public String toString() {
        return "CartDto{" +
                "id=" + id +
                ", user='" + user + '\'' +
                ", cartItemDto=" + cartItemDto +
                ", date=" + date +
                '}';
    }
}
