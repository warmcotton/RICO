package com.sws.danggeun.service;

import com.sws.danggeun.entity.Cart;
import com.sws.danggeun.entity.CartItem;
import com.sws.danggeun.entity.Item;
import com.sws.danggeun.entity.User;
import com.sws.danggeun.repository.CartItemRepository;
import com.sws.danggeun.repository.CartRepository;
import com.sws.danggeun.repository.ItemRepository;
import com.sws.danggeun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    //카트조회
    public Cart getCart(Long id) {
        return cartRepository.findById(id).get(); //NoSuchElementException
    }

    public List<Cart> getCarts(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.findByUser(user);
    }

    public List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);
        return cartItemList;
    }
    //빈 카트생성
    public Cart createCart(String email) {
        User user = userRepository.findByEmail(email).get(); //NoSuchElementException
        return cartRepository.save(Cart.getInstance(user));
    }
    //카트삭제
    public void deleteCart(Long id) {
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCart(cart);
        cartRepository.deleteById(id);
    }
    //단일아이템카트주문
    public Cart createCartWithSingleItem(Long id, int quantity, String email) {
        Cart newCart = createCart(email);
        Item item = itemRepository.findById(id).get();
        CartItem cartItem = CartItem.getInstance(quantity, item, newCart);
        cartItemRepository.save(cartItem);
        return newCart;
    }

    public Cart addItem(Long itemId, int quantity, Long cartId) {
        Cart cart = getCart(cartId);
        Item item = itemRepository.findById(itemId).get();
        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
        cartItemRepository.save(cartItem);
        return cart;
    }
}
