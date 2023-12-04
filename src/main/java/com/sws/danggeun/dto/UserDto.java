package com.sws.danggeun.dto;

import com.sws.danggeun.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class UserDto {
    private Long id;
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String name;

    public static UserDto getInstance(User user) {
        UserDto userDto = new UserDto();
        userDto.id = user.getId();
        userDto.email = user.getEmail();
        userDto.name = user.getName();
        return userDto;
    }
}
