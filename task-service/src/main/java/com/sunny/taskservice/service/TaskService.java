package com.sunny.taskservice.service;

import com.sunny.taskservice.dto.AssignMemberRequestDto;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.CreateSubTaskRequestDto;

public interface TaskService {
    Long mainTaskSave(String accessToken, CreateMainTaskRequestDto createMainTaskRequestDto);
    Long subTaskSave(String accessToken, CreateSubTaskRequestDto createSubTaskRequestDto);
    void addAssignMember(String accessToken, Long subTaskId, AssignMemberRequestDto assignMemberRequestDto);
}
