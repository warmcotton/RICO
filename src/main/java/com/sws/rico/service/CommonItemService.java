package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.entity.Item;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.OrderException;
import com.sws.rico.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommonItemService {
    private final ItemRepository itemRepository;

    protected boolean checkAndReduce(long itemId, int quantity) throws CustomException {
        if (quantity > countItemQuantity(itemId)) throw new OrderException("상품 수량 초과");
        updateItemQuantity(itemId, quantity);
        return true;
    }

    protected void updateItemQuantity(Long itemId, int quantity) {
        Item item = itemRepository.findById(itemId).get();
        int remain = item.getQuantity() - quantity;
        item.setQuantity(remain);
        if(remain == 0) item.setItemStatus(ItemStatus.SOLD_OUT);
        else item.setItemStatus(ItemStatus.FOR_SALE);
    }

    protected int countItemQuantity(Long id) {
        Item item = itemRepository.findById(id).get();
        return item.getQuantity();
    }

    protected void restore(long itemId, int quantity) {
        Item item = itemRepository.findById(itemId).get();
        item.setQuantity(item.getQuantity()+quantity);
    }
}
