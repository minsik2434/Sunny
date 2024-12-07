package com.sunny.userservice.controller;

import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.dto.LoginRequestDto;
import com.sunny.userservice.dto.TokenResponseDto;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import com.sunny.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@RequestBody UserRequestDto userRequestDto){
        userService.save(userRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<UserResponseDto> userInfo(@PathVariable String userEmail){
        UserResponseDto userResponseDto = userService.getUser(userEmail);
        return ResponseEntity.ok(userResponseDto);
    }

    @GetMapping("/user/{userEmail}/exist")
    public ResponseEntity<Void> exist(@PathVariable String userEmail){
        boolean exist = userService.exist(userEmail);
        if(!exist){
            throw new ResourceNotFoundException("User Not Found");
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        TokenResponseDto token = userService.login(loginRequestDto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/logout/{userEmail}")
    public ResponseEntity<Void> logout(@PathVariable String userEmail){
        userService.logout(userEmail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(@RequestHeader("Authorization") String refreshToken){
        String token = refreshToken.replace("Bearer ", "");
        TokenResponseDto refresh = userService.refresh(token);
        return ResponseEntity.ok(refresh);
    }
    @GetMapping("/check")
    public String check(){
        return "check";
    }
}