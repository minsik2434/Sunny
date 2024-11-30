package com.sunny.projectservice.service;

import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.ProjectResponseDto;

import java.util.List;

public interface ProjectService {
    void create(String accessToken, CreateRequestDto createRequestDto);

    List<ProjectResponseDto> getProject(String userEmail);
}
