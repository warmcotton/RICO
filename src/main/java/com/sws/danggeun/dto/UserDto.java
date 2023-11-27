package com.sws.danggeun.dto;

import com.sws.danggeun.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String name;

    public static UserDto getInstance(User user) {
        UserDto userDto = new UserDto();
        userDto.email = user.getEmail();
        userDto.name = user.getName();
        return userDto;
    }
}
