package com.sws.danggeun.entity;

import lombok.Getter;
import lombok.Setter;

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

    public static User getInstance(String email, String password, String name) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password); //인코더
        newUser.setName(name);

        return newUser;
    }
}
