package com.sws.rico.service;

import com.sws.rico.dto.UserDto;
import com.sws.rico.exception.UserException;
import com.sws.rico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CommonUserService {
    private final UserRepository userRepository;

    public UserDto getUserDtoByEmail(String email) {
        return UserDto.getUserDto(userRepository.findByEmail(email).orElseThrow(() -> new UserException("아이디 정보가 없습니다.")));
    }
}
