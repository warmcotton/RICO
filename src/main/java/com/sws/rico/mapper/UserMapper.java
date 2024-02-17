package com.sws.rico.mapper;

import com.sws.rico.dto.UserDto;
import com.sws.rico.entity.User;
import org.springframework.stereotype.Component;


@Component
public class UserMapper {
    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        userDto.setCreatedAt(user.getCreatedAt());
        return userDto;
    }
}
