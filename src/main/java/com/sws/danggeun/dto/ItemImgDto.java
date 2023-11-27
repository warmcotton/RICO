package com.sws.danggeun.dto;

import com.sws.danggeun.entity.ItemImg;

public class ItemImgDto {
    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repimgYn;

    public static ItemImgDto getInstance(ItemImg itemImg) {
        ItemImgDto itemImgDto = new ItemImgDto();
        itemImgDto.id = itemImg.getId();
        itemImgDto.imgName = itemImg.getImgName();
        itemImgDto.imgUrl = itemImg.getImgUrl();
        itemImgDto.repimgYn = itemImg.getRepimgYn();
        return itemImgDto;
    }

    @Override
    public String toString() {
        return "ItemImgDto{" +
                "id=" + id +
                ", imgName='" + imgName + '\'' +
                ", oriImgName='" + oriImgName + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", repimgYn='" + repimgYn + '\'' +
                '}';
    }
}
