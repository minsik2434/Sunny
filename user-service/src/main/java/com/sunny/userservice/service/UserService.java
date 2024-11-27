package com.sunny.userservice.service;

import com.sunny.userservice.dto.LoginRequestDto;
import com.sunny.userservice.dto.TokenResponseDto;
import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;

public interface UserService {

    void save(UserRequestDto userRequestDto);
    UserResponseDto getUser(String email);

    TokenResponseDto login(LoginRequestDto loginRequestDto);
}
