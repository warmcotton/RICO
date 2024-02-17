package com.sws.rico.dto;

import com.sws.rico.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repimgYn;
}
