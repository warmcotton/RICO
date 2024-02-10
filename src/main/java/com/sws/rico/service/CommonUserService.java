package com.sws.rico.service;

import com.sws.rico.dto.UserDto;
import com.sws.rico.entity.Cart;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.Order;
import com.sws.rico.entity.User;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.CartRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.OrderRepository;
import com.sws.rico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = {CustomException.class})
public class CommonUserService {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    protected boolean validateUserCart(Long cartId, String email) {
        Cart cart = cartRepository.findById(cartId).get();
        return email.equals(cart.getUser().getEmail());
    }

    protected boolean validateUserItem(Long itemId, String email) {
        Item item = itemRepository.findById(itemId).get();
        return email.equals(item.getUser().getEmail());
    }

    protected boolean validateUserOrder(Long orderId, String email) {
        Order order = orderRepository.findById(orderId).get();
        return email.equals(order.getUser().getEmail());
    }

    public boolean validateDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    protected User getUser(String email) throws CustomException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
    }

    public UserDto getUserDtoByEmail(String email) throws CustomException {
        return UserDto.getUserDto(userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다.")));
    }
}
