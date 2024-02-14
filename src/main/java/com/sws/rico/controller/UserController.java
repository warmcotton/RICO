package com.sws.rico.controller;

import com.sws.rico.dto.ReviewDto;
import com.sws.rico.dto.UserDto;
import com.sws.rico.exception.CustomException;
import com.sws.rico.service.CommonUserService;
import com.sws.rico.service.UserService;
import com.sws.rico.token.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final CommonUserService commonUserService;

//    @GetMapping("/signup")
//    public String signUp() {
//        return "signup";
//    }

//    @PostMapping("/signup")
//    public String register(@RequestParam("name") String name, @RequestParam("email") String email,
//                           @RequestParam("password") String password, @RequestParam("check") String check) throws CustomException {
//        if(name.isEmpty() || email.isEmpty() || password.isEmpty() || check.isEmpty() || !check.equals(password)) throw new IllegalArgumentException("Invalid Arguments");
//        return "redirect:/login";
//    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody HashMap<String, String> map, HttpServletResponse response) throws CustomException {
        if(map.get("email")==null || map.get("password")==null || map.get("email").isEmpty() || map.get("password").isEmpty()) throw new IllegalArgumentException("Invalid Arguments");
        TokenInfo token =  userService.login(map.get("email"), map.get("password"));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Expose-Headers", "*");
        headers.add("grantType",token.getGrantType());
        headers.add("accessToken",token.getAccessToken());
        headers.add("refreshToken",token.getRefreshToken());

        return ResponseEntity.status(200).headers(headers).build();
    }

//    @GetMapping("/login")
//    public String login() {
//        return "login";
//    }

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

    @PostMapping("/register")
    public UserDto registerUser(@RequestBody HashMap<String, String> map) throws CustomException {
        checkRegisterInfo(map);
        return userService.registerNewUser(map.get("email"),map.get("password"),map.get("name"));
    }

    @PostMapping("/register/supplier")
    public UserDto registerSupplier(@RequestBody HashMap<String, String> map) throws CustomException {
        checkRegisterInfo(map);
        return userService.registerNewSupplier(map.get("email"),map.get("password"),map.get("name"));
    }

    @PostMapping("/review")
    public ResponseEntity<List<ReviewDto>> submitReview(@RequestBody @Valid ReviewDto ReviewDto, Authentication authentication) throws CustomException {
        List<ReviewDto> tmp = userService.submitReview(ReviewDto, authentication.getName());
        return ResponseEntity.ok(tmp);
    }

    @GetMapping("/reviews/{itemId}")
    public ResponseEntity<List<ReviewDto>> getReview(@PathVariable Long itemId) throws CustomException {
        //num = 0,1,2,3,4 ....

        List<ReviewDto> tmp = userService.getReview(itemId);

        return ResponseEntity.ok(tmp);
    }

    @GetMapping("/user")
    public UserDto user(Authentication authentication) throws CustomException {
        return commonUserService.getUserDtoByEmail(authentication.getName());
    }

    @PutMapping("/user")
    public UserDto update(@RequestBody @Valid UserDto userDto, Authentication authentication) throws CustomException {
        return userService.update(userDto, authentication.getName());
    }

    @GetMapping("/user/{userId}")
    public UserDto user(@PathVariable Long userId) throws CustomException {
        if(userId<1) throw new IllegalArgumentException("Invalid Arguments");
        return userService.getUserDtoById(userId);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() throws CustomException {
        return userService.getUserDtoList();
    }

    private String getToken(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer")) {
            token = token.substring(7);
        } else token = null;
        return token;
    }

    private void checkRegisterInfo(HashMap<String, String> map) {
        if(map.get("email")==null || map.get("password")==null || map.get("name")==null ||
                map.get("email").isEmpty() || map.get("password").isEmpty() || map.get("name").isEmpty()) throw new IllegalArgumentException("Invalid Arguments");

        if (!(map.get("email").matches("^[a-zA-Z0-9]{2,20}+@[0-9a-zA-Z]+\\.[a-z]+$"))
                || !(map.get("password").matches("^[a-zA-Z0-9!@#$]{8,20}$") || !(map.get("name").matches("^[^\\s]{2,20}$")))) throw new IllegalArgumentException("Invalid Arguments");
    }

}
