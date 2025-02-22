package com.sws.rico.dto;

import com.sws.rico.constant.CategoryDto;
import com.sws.rico.entity.CategoryWrapper;
import com.sws.rico.entity.Item;
import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter @Setter
public class ItemDto {
    private Long id;
    @NotEmpty
    private String name;
    @NotNull
    @Min(1000)
    private int price;
    @NotNull
    @Min(1)
    private int quantity;
    @NotNull
    private ItemStatus itemStatus;
    @NotEmpty
    @Size(min = 5, max = 50)
    private String brief;
    @NotEmpty
    @Size(min = 10, max = 2000)
    private String description;
    @NotEmpty
    private List<CategoryDto> category;
    private List<ItemImgDto> itemImgDtoList;
    private String user;
}
