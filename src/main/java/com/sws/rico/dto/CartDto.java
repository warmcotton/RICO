package com.sws.rico.dto;

import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
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
    private Long id;
    private LocalDateTime date;
    private List<CartItemDto> cartItemDto;
    private String user;

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
