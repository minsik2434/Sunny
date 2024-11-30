package com.sunny.projectservice.service;

import com.sunny.projectservice.dto.CreateRequestDto;

public interface ProjectService {
    void create(String accessToken, CreateRequestDto createRequestDto);
}
