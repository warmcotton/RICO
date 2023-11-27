package com.sws.danggeun.entity;

import com.sws.danggeun.constant.Role;
import com.sws.danggeun.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id @Column(name = "user_id") @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public static User getInstance(String email, String password, String name, PasswordEncoder passwordEncoder) {
        User newUser = new User();
        newUser.email = email;
        newUser.password = passwordEncoder.encode(password); //인코더
        newUser.name = name;
        newUser.role = Role.USER;
        return newUser;
    }

    public User setInstance(UserDto userDto, PasswordEncoder passwordEncoder) {
        this.email = userDto.getEmail();
        this.password = passwordEncoder.encode(userDto.getPassword());
        this.name = userDto.getName();
        return this;
    }
}
