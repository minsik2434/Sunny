package com.sunny.projectservice.service;

import com.sunny.projectservice.client.UserServiceClient;
import com.sunny.projectservice.common.JwtUtil;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final UserServiceClient userServiceClient;
    private final JwtUtil jwtUtil;
    @Override
    public void create(String accessToken, CreateRequestDto createRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        UserResponseDto user = userServiceClient.getUser(userEmail);
    }

}
