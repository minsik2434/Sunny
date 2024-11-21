package com.sunny.userservice.controller;

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
}