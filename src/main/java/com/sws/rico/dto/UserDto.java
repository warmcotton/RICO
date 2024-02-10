package com.sws.rico.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sws.rico.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
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
