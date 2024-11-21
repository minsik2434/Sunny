package com.sunny.userservice.service;

import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    void save(UserRequestDto userRequestDto);
    UserResponseDto getUser(String userId);
}
