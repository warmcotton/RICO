package com.sws.rico.service;

import com.sws.rico.entity.Item;
import com.sws.rico.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommonUserService {
    private final ItemRepository itemRepository;

    protected boolean checkUser(Long itemId, String email) {
        Item item = itemRepository.findById(itemId).get();
        return email.equals(item.getUser().getEmail());
    }
}