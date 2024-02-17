package com.sws.rico.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Table(name="item_img")
@Getter @Setter
public class ItemImg {
    @Id @Column(name="item_img_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imgName; 
    private String oriImgName; 
    private String imgUrl; 
    private String repimgYn; 
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    public static ItemImg createItemImg(String imgName, String oriImgName, String imgUrl, String repimgYn, Item item) {
        ItemImg itemImg = new ItemImg();
        itemImg.imgName = imgName;
        itemImg.oriImgName = oriImgName;
        itemImg.imgUrl = imgUrl;
        itemImg.repimgYn = repimgYn;
        itemImg.item = item;
        return itemImg;
    }
}
