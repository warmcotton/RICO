package com.sws.rico.dto;

import lombok.Getter;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
public class CartContainerDto {
    @Size(min = 1)
    private List<@Valid CartDto> cartDtoList;
}
