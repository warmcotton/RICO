package com.sws.danggeun.service;

import com.sws.danggeun.constant.ItemStatus;
import com.sws.danggeun.entity.*;
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
        return cartRepository.findById(id).get();
    }
    //카트목록조회
    public List<Cart> getCarts(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.findByUser(user);
    }
    //카트상품조회
    public CartItem getCartItem(Long id) { return cartItemRepository.findById(id).get(); }
    //카트상품목록조회
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
    public Cart createCartWithSingleItem(Long id, int quantity, String email) throws Exception {
        Cart newCart = createCart(email);
        Item item = itemRepository.findById(id).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new Exception("판매중 아님");
        CartItem cartItem = CartItem.getInstance(quantity, item, newCart);
        cartItemRepository.save(cartItem);
        return newCart;
    }
    //카트상품담기
    public Cart addItemToCart(Long itemId, int quantity, Long cartId) throws Exception {
        Cart cart = getCart(cartId);
        Item item = itemRepository.findById(itemId).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new Exception("판매중 아님");
        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
        cartItemRepository.save(cartItem);
        return cart;
    }
    //카트상품삭제
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }
}
