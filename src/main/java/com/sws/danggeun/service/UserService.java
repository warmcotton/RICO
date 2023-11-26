package com.sws.danggeun.service;

import com.sws.danggeun.entity.User;
import com.sws.danggeun.repository.UserRepository;
import com.sws.danggeun.token.JwtTokenProvider;
import com.sws.danggeun.token.TokenInfo;
import com.sws.danggeun.token.TokenUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    //회원가입
    public User registerNewUser(String email, String password, String name) throws Exception {
        if (validateDuplicateEmail(email)) throw new Exception("이메일 중복");
        User user = User.getInstance(email, password, name, passwordEncoder);
        return userRepository.save(user);
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
    public TokenInfo login(String email, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(token);
        return jwtTokenProvider.generateToken(authentication);
    }
    //구매내역

    //판매내역

    //활동기록

    //회원조회
    
    //회원정보
    
    //회원정보수정
}
