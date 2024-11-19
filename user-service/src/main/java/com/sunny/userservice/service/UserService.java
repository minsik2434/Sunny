package com.sunny.userservice.service;

import com.sunny.userservice.domain.dto.UserRequestDto;

public interface UserService {
    void save(UserRequestDto userRequestDto);
}
