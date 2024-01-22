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

    public static ItemImgDto getItemImgDto(ItemImg itemImg) {
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
