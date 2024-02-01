package com.sws.rico.entity;

import com.sws.rico.constant.CategoryDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "category")
@Getter
@Setter
public class CategoryWrapper {
    @Id
    @Column(name = "category_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "item_id") @ManyToOne(fetch = FetchType.LAZY)
    private Item item;
    @Enumerated(EnumType.STRING)
    private CategoryDto category;

    public static CategoryWrapper getInstance(CategoryDto cate, Item item) {
        CategoryWrapper cartegory = new CategoryWrapper();
        cartegory.setCategory(cate);
        cartegory.setItem(item);
        return cartegory;
    }
}
