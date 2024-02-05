package com.sws.rico.dto;

import com.sws.rico.entity.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;

    public static UserDto getUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.id = user.getId();
        userDto.email = user.getEmail();
        userDto.name = user.getName();
        userDto.createdAt = user.getCreatedAt();
        return userDto;
    }
}
