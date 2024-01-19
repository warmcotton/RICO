package com.sws.rico.repository;

import com.sws.rico.entity.Item;
import com.sws.rico.entity.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
    void deleteAllByItem(Item item);
    List<ItemImg> findByItem(Item i);
}
