package com.sunny.taskservice.service;

import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.common.client.ProjectClient;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.PermissionException;
import com.sunny.taskservice.repository.MainTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService{
    private final MainTaskRepository mainTaskRepository;
    private final ProjectClient projectClient;
    private final JwtUtil jwtUtil;
    @Override
    public Long mainTaskSave(String accessToken, CreateMainTaskRequestDto createMainTaskRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Long projectId = createMainTaskRequestDto.getProjectId();
        ProjectMemberDto projectMember = projectClient.getProjectMember(projectId, userEmail);
        if(!projectMember.getRole().equals("OWNER")){
            throw new PermissionException("lack of permission");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        MainTask mainTask = new MainTask(
                createMainTaskRequestDto.getName(),
                createMainTaskRequestDto.getDescription(),
                LocalDateTime.parse(createMainTaskRequestDto.getStartDateTime(), formatter),
                LocalDateTime.parse(createMainTaskRequestDto.getDeadLine(), formatter),
                createMainTaskRequestDto.getProjectId()
        );

        MainTask savedMainTask = mainTaskRepository.save(mainTask);
        return savedMainTask.getId();
    }
}
