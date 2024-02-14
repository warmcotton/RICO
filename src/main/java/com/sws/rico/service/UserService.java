package com.sws.rico.service;

import com.sws.rico.constant.Role;
import com.sws.rico.dto.ReviewDto;
import com.sws.rico.dto.UserDto;
import com.sws.rico.entity.Item;
import com.sws.rico.entity.Review;
import com.sws.rico.entity.User;
import com.sws.rico.exception.ItemException;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.ItemRepository;
import com.sws.rico.repository.ReviewRepository;
import com.sws.rico.repository.UserRepository;
import com.sws.rico.token.JwtTokenProvider;
import com.sws.rico.token.TokenInfo;
import com.sws.rico.token.TokenUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CommonUserService commonUserService;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${access.expires.time}")
    private long ACCESS_EXPIRES_TIME;
    @Value("${refresh.expires.time}")
    private long REFRESH_EXPIRES_TIME;

    public UserDto registerNewUser(String email, String password, String name) {
        if (validateDuplicateEmail(email)) throw new UserException("중복된 이메일입니다.");
        User user = userRepository.save(User.createUser(email, password, name, passwordEncoder));
        return UserDto.getUserDto(user);
    }

    public UserDto registerNewSupplier(String email, String password, String name) {
        if (validateDuplicateEmail(email)) throw new UserException("중복된 이메일입니다.");
        User user = userRepository.save(User.createSupplier(email, password, name, passwordEncoder));
        return UserDto.getUserDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email)
                .map(TokenUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException("아이디 정보가 없습니다."));
    }

    public TokenInfo login(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);

        try {
            User user = getUser(email);
            if (!passwordEncoder.matches(password, user.getPassword())) throw new IllegalArgumentException("");
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token); //inner transactianl exeption
            TokenInfo authenticated = jwtTokenProvider.generateToken(authentication);
            redisTemplate.opsForValue().set("RT:"+authenticated.getRefreshToken(),email,REFRESH_EXPIRES_TIME, TimeUnit.MILLISECONDS);
            return authenticated;
        } catch (AuthenticationException e) {
            throw new UserException("인증 오류");
        } catch (NoSuchElementException e) {
            throw new UserException("아이디 확인");
        } catch (IllegalArgumentException e) {
            throw new UserException("비밀번호 확인");
        }
    }

    public void logout(String token, String email) {
        redisTemplate.opsForValue().set("LOGOUT:"+token,email,ACCESS_EXPIRES_TIME,TimeUnit.MILLISECONDS);
    }

    public TokenInfo refresh(String token) {
        if(redisTemplate.opsForValue().get("RT:"+token)==null) throw new UserException("not valid refresh token");
        String email = redisTemplate.opsForValue().get("RT:"+token);
        redisTemplate.delete("RT:"+token);
        Role userRole = getUser(email).getRole();
        Authentication authentication = jwtTokenProvider.getRefreshAuthentication(email, userRole);
        TokenInfo authenticated = jwtTokenProvider.generateToken(authentication);
        redisTemplate.opsForValue().set("RT:"+authenticated.getRefreshToken(),email,REFRESH_EXPIRES_TIME, TimeUnit.MILLISECONDS);
        return authenticated;
    }

    public List<UserDto> getUserDtoList() {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            userDtos.add(commonUserService.getUserDtoByEmail(user.getEmail()));
        }
        return userDtos;
    }

    public UserDto update(UserDto userDto, String email) {
        User user = getUser(email);
        if (validateDuplicateEmail(userDto.getEmail())) throw new UserException("중복된 이메일입니다.");
        return UserDto.getUserDto(user.setInstance(userDto, passwordEncoder));
    }

    public UserDto getUserDtoById(Long userId) {
        return UserDto.getUserDto(userRepository.findById(userId).orElseThrow(() -> new UserException("아이디 정보가 없습니다.")));
    }

    public List<ReviewDto> submitReview(ReviewDto reviewDto, String email) {
        Item item = itemRepository.findById(reviewDto.getItemId()).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        User user = getUser(email);

        if(reviewRepository.findByItemAndUser(item, user).isPresent()) {
            throw new UserException("리뷰가 이미 작성되었습니다.");
        }
        Review review = Review.getInstance(item, user, reviewDto.getRating(), reviewDto.getReview());
        reviewRepository.save(review);

        return reviewRepository.findAll().stream().map(ReviewDto::getReviewDto).collect(toList());
    }

    public List<ReviewDto> getReview(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemException("상품 정보가 없습니다."));
        return reviewRepository.findAllByItem(item).stream().map(ReviewDto::getReviewDto).collect(toList());
    }

    private boolean validateDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다."));
    }
}
