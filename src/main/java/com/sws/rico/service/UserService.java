package com.sws.rico.service;

import com.sws.rico.constant.Role;
import com.sws.rico.dto.UserDto;
import com.sws.rico.entity.User;
import com.sws.rico.exception.CustomException;
import com.sws.rico.exception.UserException;
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

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
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
        return UserDto.getUserDto(user);
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

    public TokenInfo login(String email, String password) throws CustomException {
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
        return UserDto.getUserDto(getUser(email));
    }
    
    public List<UserDto> getUserDtoList() {
        return userRepository.findAll().stream().map(user -> getUserDtoByEmail(user.getEmail())).collect(Collectors.toList());
    }
    //회원정보수정
    public UserDto update(UserDto userDto, String email) throws CustomException {
        User user = getUser(email);
        if (validateDuplicateEmail(userDto.getEmail())) throw new UserException("이메일 중복");
        return UserDto.getUserDto(user.setInstance(userDto, passwordEncoder));
    }

    public UserDto getUserDtoById(Long userId) {
        return UserDto.getUserDto(userRepository.findById(userId).get());
    }
}
