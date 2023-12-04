package com.sws.danggeun.service;

import com.sws.danggeun.constant.Role;
import com.sws.danggeun.dto.UserDto;
import com.sws.danggeun.entity.User;
import com.sws.danggeun.exception.CustomException;
import com.sws.danggeun.exception.UserException;
import com.sws.danggeun.repository.UserRepository;
import com.sws.danggeun.token.JwtTokenProvider;
import com.sws.danggeun.token.TokenInfo;
import com.sws.danggeun.token.TokenUserDetails;
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

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${access.expires.time}")
    private long ACCESS_EXPIRES_TIME;
    @Value("${refresh.expires.time}")
    private long REFRESH_EXPIRES_TIME;
    //회원가입
    public UserDto registerNewUser(String email, String password, String name) throws CustomException {
        if (validateDuplicateEmail(email)) throw new UserException("이메일 중복");
        User user = userRepository.save(User.getInstance(email, password, name, passwordEncoder));
        return UserDto.getInstance(user);
    }
    //중복확인
    public boolean validateDuplicateEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(TokenUserDetails::new)
                .orElseThrow(()->new UsernameNotFoundException("유저 존재 x"));
    }
    //로그인
    public TokenInfo login(String email, String password) throws CustomException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
            TokenInfo authenticated = jwtTokenProvider.generateToken(authentication);
            redisTemplate.opsForValue().set("RT:"+authenticated.getRefreshToken(),email,REFRESH_EXPIRES_TIME, TimeUnit.MILLISECONDS);
            return authenticated;
        } catch (AuthenticationException e) {
            throw new UserException("인증 오류");
        }
    }

    public void logout(String token, String email) {
        redisTemplate.opsForValue().set("LOGOUT:"+token,email,ACCESS_EXPIRES_TIME,TimeUnit.MILLISECONDS);
    }
    public TokenInfo refresh(String token) throws CustomException {
        if(redisTemplate.opsForValue().get("RT:"+token)==null) throw new UserException("not valid refresh token");
        String email = redisTemplate.opsForValue().get("RT:"+token);
        redisTemplate.delete("RT:"+token);
        Role userRole = getUser(email).getRole();
        Authentication authentication = jwtTokenProvider.getRefreshAuthentication(email, userRole);
        TokenInfo authenticated = jwtTokenProvider.generateToken(authentication);
        redisTemplate.opsForValue().set("RT:"+authenticated.getRefreshToken(),email,REFRESH_EXPIRES_TIME, TimeUnit.MILLISECONDS);
        return authenticated;
    }
    //회원조회
    private User getUser(String email) {
        return userRepository.findByEmail(email).get();
    }

    public UserDto getUserDtoByEmail(String email) {
        return UserDto.getInstance(getUser(email));
    }
    
    public List<UserDto> getUserDtoList() {
        return userRepository.findAll().stream().map(user -> getUserDtoByEmail(user.getEmail())).collect(Collectors.toList());
    }
    //회원정보수정
    public UserDto update(UserDto userDto, String email) throws CustomException {
        User user = getUser(email);
        if (validateDuplicateEmail(userDto.getEmail())) throw new UserException("이메일 중복");
        return UserDto.getInstance(user.setInstance(userDto, passwordEncoder));
    }

    public UserDto getUserDtoById(Long userId) {
        return UserDto.getInstance(userRepository.findById(userId).get());
    }
}
