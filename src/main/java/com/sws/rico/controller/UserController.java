package com.sws.rico.controller;

import com.sws.rico.dto.UserDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.UserService;
import com.sws.rico.token.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public TokenInfo login(@RequestBody HashMap<String, String> map) throws CustomException {
        if(map.get("email")==null || map.get("password")==null || map.get("email").isEmpty() || map.get("password").isEmpty()) throw new IllegalArgumentException("Invalid Arguments");
        return userService.login(map.get("email"), map.get("password"));
    }

    @ResponseBody
    @GetMapping("/refresh")
    public TokenInfo refresh(@RequestHeader("Authorization") String token) throws CustomException {
        token = getToken(token);
        if(token == null) throw new IllegalArgumentException("Invalid token");
        return userService.refresh(token);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, Authentication authentication) {
        token = getToken(token);
        if(token == null) throw new IllegalArgumentException("Invalid token");
        userService.logout(token, authentication.getName());
        return ResponseEntity.status(200).build();
    }

    @ResponseBody
    @PostMapping("/register")
    public UserDto register(@RequestBody HashMap<String, String> map) throws CustomException {
        if(map.get("email")==null || map.get("password")==null ||  map.get("name")==null || map.get("email").isEmpty()||
                map.get("password").isEmpty() || map.get("name").isEmpty()  ) throw new IllegalArgumentException("Invalid Arguments");
        return userService.registerNewUser(map.get("email"), map.get("password"), map.get("name"));
    }

    @ResponseBody
    @GetMapping("/user")
    public UserDto user(Authentication authentication) {
        return userService.getUserDtoByEmail(authentication.getName());
    }

    @ResponseBody
    @PutMapping("/user")
    public UserDto update(@RequestBody @Valid UserDto userDto, Authentication authentication) throws CustomException {
        return userService.update(userDto, authentication.getName());
    }

    @ResponseBody
    @GetMapping("/user/{userId}")
    public UserDto user(@PathVariable Long userId) {
        if(userId<1) throw new IllegalArgumentException("Invalid Arguments");
        return userService.getUserDtoById(userId);
    }

    @ResponseBody
    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return userService.getUserDtoList();
    }

    private String getToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        } else token = null;
        return token;
    }

    //deleteUsers
}