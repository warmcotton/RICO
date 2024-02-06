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
import com.sws.rico.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ItemRepository itemRepository;
    private final ItemImgRepository itemImgRepository;
    private final CommonUserService commonUserService;

    public CartDto createCart(String email) throws CartException {
        User user = userRepository.findByEmail(email).get();
        if(cartRepository.findByUser(user).isPresent()) throw new CartException("카트가 이미 생성됨");
        return getCartDto(createCartByEmail(email),new ArrayList<>());
    }

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

    public CartDto addItemToCartv2(Long itemId, int quantity, String email) throws CustomException {
        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUser(user).get();
        Item item = itemRepository.findById(itemId).get();

        if(item.getItemStatus()== ItemStatus.SOLD_OUT) throw new ItemException("판매중 아님");
        if(item.getUser().getEmail().equals(email)) throw new CartException("본인이 판매하는 상품 주문 x");
        List<Long> itemIdList = cartItemRepository.findByCart(cart).stream().map(ct -> ct.getItem().getId()).collect(Collectors.toList());
        if(itemIdList.contains(itemId)) throw new CartException("이미 추가된 상품입니다.");

        CartItem cartItem = CartItem.getInstance(quantity, item, cart);
        cartItemRepository.save(cartItem);
        return getCartDto(cart, getCartItemsByCartId(cart.getId()));
    }

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

    public CartDto getMyCart(String email) {
        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUser(user).get();
        List<CartItem> cartItems = getCartItemsByCartId(cart.getId());
        return getCartDto(cart, cartItems);
    }

    public CartItemDto changeCartItemQuantity(Long itemId, int count, String email) throws CustomException {
        User user = userRepository.findByEmail(email).get();
        Cart cart = cartRepository.findByUser(user).get();

        if(!commonUserService.validateUserCart(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
        List<Long> itemIdList = cartItemRepository.findByCart(cart).stream()
                .map(cartItem_ -> cartItem_.getItem().getId()).collect(Collectors.toList());
        if (!itemIdList.contains(itemId)) throw new CartException("카트에 상품 없음");

        CartItem cartItem = cartItemRepository.findById(itemIdList.get(itemIdList.indexOf(itemId))).get();
        cartItem.setCount(count);
        return CartItemDto.getInstance(cartItem, getItemImgs(cartItem.getItem()));
    }

    public void deleteCartItem(Long cartItemId, String email) throws CustomException {
        CartItem cartItem = cartItemRepository.findById(cartItemId).get();
        Cart cart = cartItem.getCart();
        if(!commonUserService.validateUserCart(cart.getId(), email)) throw new CartException("카트 접근 권한 없음");
        cartItemRepository.deleteById(cartItemId);
    }

    public void deleteCart(Long id, String email) throws CustomException {
        if(!commonUserService.validateUserCart(id, email)) throw new CartException("카트 접근 권한 없음");
        Cart cart = getCart(id);
        cartItemRepository.deleteAllByCart(cart);
        cartRepository.deleteById(id);
    }

    private Cart createCartByEmail(String email) {
        User user = userRepository.findByEmail(email).get();
        return cartRepository.save(Cart.getInstance(user));
    }

    private List<CartItem> getCartItemsByCartId(Long cartId) {
        Cart cart = getCart(cartId);
        List<CartItem> cartItemList = cartItemRepository.findByCart(cart);
        return cartItemList;
    }

    private Cart getCart(Long id) {
        return cartRepository.findById(id).get();
    }

    private List<ItemImg> getItemImgs(Item i) {
        return itemImgRepository.findAllByItem(i);
    }

    private CartDto getCartDto(Cart c, List<CartItem> cartItemList) {
        CartDto cartDto = new CartDto();
        cartDto.setDate(c.getCreatedAt());
        cartDto.setCartItemDto(cartItemList.stream().map(cartItem -> CartItemDto.getInstance(cartItem, getItemImgs(cartItem.getItem()))).collect(Collectors.toList()));
        cartDto.setUser(c.getUser().getEmail());
        return cartDto;
    }
}
