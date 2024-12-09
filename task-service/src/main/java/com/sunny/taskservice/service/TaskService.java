package com.sunny.taskservice.service;

import com.sunny.taskservice.dto.CreateMainTaskRequestDto;

public interface TaskService {
    Long mainTaskSave(String accessToken, CreateMainTaskRequestDto createMainTaskRequestDto);
}
