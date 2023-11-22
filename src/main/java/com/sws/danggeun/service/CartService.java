package com.sws.danggeun.service;

import com.sws.danggeun.entity.Cart;
import com.sws.danggeun.entity.User;
import com.sws.danggeun.repository.CartRepository;
import com.sws.danggeun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    //카트조회
    public Cart searchCart(Long id) {
        return cartRepository.findById(id).get(); //NoSuchElementException
    }
    //카트생성
    public Cart createCart(String email) {
        User user = userRepository.findByEmail(email).get(); //NoSuchElementException
        return cartRepository.save(Cart.newInstance(user.getId()));
    }
    //카트삭제
    public void deleteCart(Long id) {
        cartRepository.deleteById(id); //NoSuchElementException
    }
    //카트주문



}
