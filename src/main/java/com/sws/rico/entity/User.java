package com.sws.rico.entity;

import com.sws.rico.constant.Role;
import com.sws.rico.dto.UserDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    public static User createUser(String email, String password, String name, Role role, PasswordEncoder passwordEncoder) {
        User newUser = new User();
        newUser.email = email;
        newUser.password = passwordEncoder.encode(password); //인코더
        newUser.name = name;
        newUser.role = role;
        return newUser;
    }

    public User updateUser(UserDto userDto, PasswordEncoder passwordEncoder) {
        this.email = userDto.getEmail();
        this.password = passwordEncoder.encode(userDto.getPassword());
        this.name = userDto.getName();
        return this;
    }
}
