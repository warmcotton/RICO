package com.sws.rico.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sws.rico.entity.Cart;
import com.sws.rico.entity.CartItem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class CartDto {
    private Long id;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private List<CartItemDto> cartItemDto;
    private String user;
}
