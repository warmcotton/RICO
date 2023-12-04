package com.sws.danggeun.repository;

import com.sws.danggeun.entity.Item;
import com.sws.danggeun.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    void deleteAllByItem(Item item);
    List<ItemImg> findByItem(Item i);
}
