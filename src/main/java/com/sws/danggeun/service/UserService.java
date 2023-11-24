package com.sws.danggeun.service;

import com.sws.danggeun.dto.UserDto;
import com.sws.danggeun.entity.User;
import com.sws.danggeun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //회원가입
    public User 회원가입(UserDto userDto) throws Exception {
        if (중복확인(userDto.getEmail())) throw new Exception("이메일 중복");
        User user = User.getInstance(userDto.getEmail(), userDto.getPassword(), userDto.getName());
        return userRepository.save(user);
    }
    //중복확인
    public boolean 중복확인(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
    //로그인

    //구매내역

    //판매내역

    //활동기록

    //회원조회
    
    //회원정보
    
    //회원정보수정
}
