package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.repository.CartItemRepository;
import com.sws.rico.repository.CartRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
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
    protected Cart getCart(Long id) {
        return cartRepository.findById(id).get();
    }
    //카트목록조회
//    private List<Cart> getCartsByEmail(String email) {
//        User user = userRepository.findByEmail(email).get();
//        return cartRepository.findByUser(user);
//    }
    //카트상품조회
    private CartItem getCartItem(Long id) { return cartItemRepository.findById(id).get(); }
    //카트상품목록조회
    private List<CartItem> getCartItemsByCartId(Long cartId) {
        Cart cart = getCart(cartId);
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);
        return cartItemList;
    }

    public Page<CartDto> getCartDtosByEmail(String email, Pageable page) {
        User user = userRepository.findByEmail(email).get();

        return cartRepository.findByUser(user, page)
                .map(cart -> CartDto.getCartDto(cart, getCartItemsByCartId(cart.getId())));
    }

    //빈 카트생성
    private Cart createCartByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.save(Cart.getInstance(user));
    }

    public CartDto createCart(String email) {
        return CartDto.getCartDto(createCartByEmail(email),new ArrayList<>());
    }
    //카트삭제
    public void deleteCart(Long id, String email) throws CustomException {
        if(!checkUser(id, email)) throw new CartException("카트 접근 권한 없음");
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCart(cart);
        cartRepository.deleteById(id);
    }
    //단일아이템카트주문
//    public Cart createCartWithSingleItem(Long id, int quantity, String email) throws CustomException {
//        Cart newCart = createCartByEmail(email);
//        Item item = itemRepository.findById(id).get();
//        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
//        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
//        CartItem cartItem = CartItem.getInstance(quantity, item, newCart);
//        cartItemRepository.save(cartItem);
//        return newCart;
//    }
    //카트상품담기
    public CartDto addItemToCart(Long itemId, int quantity, Long cartId, String email) throws CustomException {
        if(!checkUser(cartId, email)) throw new CartException("카트 접근 권한 없음");
        Cart cart = getCart(cartId);
        Item item = itemRepository.findById(itemId).get();
        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
        cartItemRepository.save(cartItem);
        return CartDto.getCartDto(cart, getCartItemsByCartId(cartId));
    }
    //카트상품삭제
    public void deleteCartItem(Long cartItemId, String email) throws CustomException {
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        Cart cart = cartItem.getCart();
        if(!checkUser(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
        cartItemRepository.deleteById(cartItemId);
    }

    public CartDto getCartDto(Long cartId, String email) throws CustomException {
        if(!checkUser(cartId, email)) throw new CartException("카트 접근 권한 없음");
        return CartDto.getCartDto(getCart(cartId), getCartItemsByCartId(cartId));
    }

    private boolean checkUser(Long id, String email) {
        return email.equals(getCart(id).getUser().getEmail());
    }

    public CartItemDto getCartItemDto(Long cartItemId, String email) throws CustomException {
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        Cart cart = cartItem.getCart();
        if(!checkUser(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
        return CartItemDto.getInstance(cartItem);
    }

    public CartItemDto changeCartItemQuantity(Long cartItemId, int count, String email) throws CustomException {
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        Cart cart = cartItem.getCart();
        if(!checkUser(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
        cartItem.setCount(count);
        return CartItemDto.getInstance(cartItem);
    }
}
