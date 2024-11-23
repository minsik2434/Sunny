package com.sunny.userservice.service;

import com.sunny.userservice.dto.UserRequestDto;
import com.sunny.userservice.dto.UserResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void save(UserRequestDto userRequestDto);
    UserResponseDto getUser(String email);
}
