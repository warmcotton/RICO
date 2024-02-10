package com.sws.rico.service;

import com.sws.rico.constant.ItemStatus;
import com.sws.rico.dto.CartDto;
import com.sws.rico.dto.CartItemDto;
import com.sws.rico.entity.*;
import com.sws.rico.exception.CartException;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.CartItemRepository;
import com.sws.rico.repository.CartRepository;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.UserRepository;
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackOn = {CustomException.class})
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final CommonUserService commonUserService;
    private final CommonCartService commonCartService;

    public CartDto createCart(String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        if(cartRepository.findByUser(user).isPresent()) throw new CartException("장바구니가 이미 생성되었습니다.");
        Cart cart = cartRepository.save(Cart.getInstance(user));
        return commonCartService.getCartDto(cart,new ArrayList<>());
    }

    public CartDto addItemToCart(Long itemId, int quantity, String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.getInstance(user)));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));

        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("상품 판매중이 아닙니다.");
        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품은 주문할 수 없습니다.");
        List<Long> itemIdList = cartItemRepository.findByCart(cart).stream().map(ct -> ct.getItem().getId()).collect(Collectors.toList());
        if(itemIdList.contains(itemId)) throw new CartException("이미 추가된 상품입니다.");

        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
        cartItemRepository.save(cartItem);
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);

        return commonCartService.getCartDto(cart, cartItems);
    }

    public CartDto getMyCart(String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        if (cartRepository.findByUser(user).isEmpty()) return null;
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니 정보가 없습니다."));
        List<CartItem> cartItems = commonCartService.getCartItemsByCartId(cart.getId());
        return commonCartService.getCartDto(cart, cartItems);
    }

    public CartItemDto changeCartItemQuantity(Long itemId, int count, String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니 정보가 없습니다."));
        CartItem cartItem = null;

        for(CartItem rt : cartItemRepository.findByCart(cart)) {
            if (rt.getItem().getId() == itemId) {
                cartItem = rt;
            }
        }
        if (cartItem == null) {
            throw new CartException("장바구니에 해당 상품이 없습니다.");
        }
        cartItem.setCount(count);
        return CartItemDto.getInstance(cartItem, itemImgRepository.findAllByItem(cartItem.getItem()));
    }

    public void deleteCartItem(Long itemId, String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니 정보가 없습니다."));

        CartItem cartItem = null;
        for(CartItem rt : cartItemRepository.findByCart(cart)) {
            if (rt.getItem().getId() == itemId) {
                cartItem = rt;
            }
        }
        if (cartItem == null) {
            throw new CartException("장바구니에 상품이 없습니다.");
        }
        cartItemRepository.deleteById(cartItem.getId());
    }

    public void deleteCart(String email) throws CustomException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
        Cart cart =cartRepository.findByUser(user).orElseThrow(() -> new CartException("장바구니 정보가 없습니다."));

        cartItemRepository.deleteAllByCart(cart);
        cartRepository.deleteById(cart.getId());
    }



//    private Cart getCart(Long id) {
//        return cartRepository.findById(id).get();
//    }

//    public CartDto addItemToCart(Long itemId, int quantity, Long cartId, String email) throws CustomException {
//        if(!commonUserService.validateUserCart(cartId, email)) throw new CartException("카트 접근 권한 없음");
//        Cart cart = getCart(cartId);
//        Item item = itemRepository.findById(itemId).get();
//
//        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
//        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
//        List<Long> itemIdList = cartItemRepository.findByCart(cart).stream().map(ct -> ct.getItem().getId()).collect(Collectors.toList());
//        if(itemIdList.contains(itemId)) throw new CartException("이미 추가된 상품입니다.");
//
//        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
//        cartItemRepository.save(cartItem);
//        return getCartDto(cart, getCartItemsByCartId(cartId));
//    }

//    private Cart createCartByEmail(String email) {
//        User user = userRepository.findByEmail(email).get();
//        return cartRepository.save(Cart.getInstance(user));
//    }

//    public CartItemDto getCartItemDto(Long cartItemId, String email) throws CustomException {
//        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
//        Cart cart = cartItem.getCart();
//        if(!commonUserService.validateUserCart(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
//        return CartItemDto.getInstance(cartItem, getItemImgs(cartItem.getItem()));
//    }
//
//    public CartDto getCartDto(Long cartId, String email) throws CustomException {
//        if(!commonUserService.validateUserCart(cartId, email)) throw new CartException("카트 접근 권한 없음");
//        return getCartDto(getCart(cartId), getCartItemsByCartId(cartId));
//    }
//
//    public Page<CartDto> getMyCartPage(String email, Pageable page) {
//        User user = userRepository.findByEmail(email).get();
//
//        return cartRepository.findAllByUser(user, page)
//                .map(cart -> getCartDto(cart, getCartItemsByCartId(cart.getId())));
//    }

}
