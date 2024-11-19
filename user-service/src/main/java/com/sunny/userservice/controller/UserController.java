package com.sunny.userservice.controller;

import com.sunny.userservice.domain.dto.UserRequestDto;
import com.sunny.userservice.domain.dto.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {

    @PostMapping("/user")
    public String createUser(@RequestBody UserRequestDto userRequestDto){
        log.info("{}",userRequestDto.getEmail());
        return "";
    }
}
