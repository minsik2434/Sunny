package com.sunny.taskservice.service;

import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.common.client.ProjectClient;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.domain.SubTask;
import com.sunny.taskservice.domain.TaskMember;
import com.sunny.taskservice.dto.AssignMemberRequestDto;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.CreateSubTaskRequestDto;
import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.PermissionException;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.repository.MainTaskRepository;
import com.sunny.taskservice.repository.SubTaskRepository;
import com.sunny.taskservice.repository.TaskMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService{
    private final MainTaskRepository mainTaskRepository;
    private final SubTaskRepository subTaskRepository;
    private final ProjectClient projectClient;
    private final JwtUtil jwtUtil;
    @Override
    @Transactional
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

    @Override
    @Transactional
    public Long subTaskSave(String accessToken, CreateSubTaskRequestDto createSubTaskRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Long mainTaskId = createSubTaskRequestDto.getMainTaskId();
        ProjectMemberDto projectMember = projectClient.getProjectMember(createSubTaskRequestDto.getProjectId(),
                userEmail);
        if(!projectMember.getRole().equals("OWNER")){
            throw new PermissionException("lack of permission");
        }
        Optional<MainTask> optional = mainTaskRepository.findById(mainTaskId);

        if(optional.isEmpty()){
            throw new ResourceNotFoundException("Not Found MainTask");
        }
        MainTask mainTask = optional.get();

        SubTask subTask = new SubTask(
                mainTask,
                createSubTaskRequestDto.getTitle(),
                createSubTaskRequestDto.getDescription(),
                createSubTaskRequestDto.getStatus());
        if(isExistAssignMembers(createSubTaskRequestDto)){
            List<String> assignedMembers = createSubTaskRequestDto.getAssignedMembers();
            createTaskMember(assignedMembers, createSubTaskRequestDto.getProjectId(), subTask);
        }
        SubTask savedSubTask = subTaskRepository.save(subTask);
        return savedSubTask.getId();
    }

    @Override
    @Transactional
    public void addAssignMember(String accessToken, Long subTaskId, AssignMemberRequestDto assignMemberRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Long projectId = assignMemberRequestDto.getProjectId();
        ProjectMemberDto projectMember = projectClient.getProjectMember(projectId, userEmail);
        if(!projectMember.getRole().equals("OWNER")){
            throw new PermissionException("lack of permission");
        }
        Optional<SubTask> optional = subTaskRepository.findById(subTaskId);

        if(optional.isEmpty()){
            throw new ResourceNotFoundException("SubTask Not Found");
        }

        SubTask subTask = optional.get();
        List<String> assignMembers = assignMemberRequestDto.getAssignedMembers();
        createTaskMember(assignMembers, projectId, subTask);
    }

    private boolean isExistAssignMembers(CreateSubTaskRequestDto createSubTaskRequestDto) {
        return !createSubTaskRequestDto.getAssignedMembers().isEmpty();
    }

    private void createTaskMember(List<String> assignedMembers, Long projectId, SubTask subTask) {
        assignedMembers.forEach(
                email -> {
                    projectClient.getProjectMember(projectId, email);
                    new TaskMember(email, subTask);
                }
        );
    }
}
