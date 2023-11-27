package com.sws.danggeun.controller;

import com.sws.danggeun.dto.UserDto;
import com.sws.danggeun.service.UserService;
import com.sws.danggeun.token.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public TokenInfo login(@RequestBody HashMap<String, String> map) throws Exception {
        return userService.login(map.get("email"), map.get("password"));
    }
    @ResponseBody
    @PostMapping("/register")
    public UserDto register(@RequestBody HashMap<String, String> map) throws Exception {
        return userService.registerNewUser(map.get("email"), map.get("password"), map.get("name"));
    }

    @ResponseBody
    @GetMapping("/user")
    public UserDto user(Authentication authentication) {
        return userService.getUserDto(authentication.getName());
    }

    @ResponseBody
    @PutMapping("/user")
    public UserDto update(@RequestBody UserDto userDto, Authentication authentication) throws Exception {
        return userService.update(userDto, authentication.getName());
    }
}
