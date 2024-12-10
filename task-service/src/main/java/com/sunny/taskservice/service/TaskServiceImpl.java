package com.sunny.taskservice.service;

import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.common.client.ProjectClient;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.domain.Role;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sunny.taskservice.domain.Role.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService{
    private final MainTaskRepository mainTaskRepository;
    private final SubTaskRepository subTaskRepository;
    private final ProjectClient projectClient;
    private final JwtUtil jwtUtil;
    private static final String DATE_TIME_PATTERN = "yyy-MM-dd HH:mm:ss";
    @Override
    @Transactional
    public Long mainTaskSave(String accessToken, CreateMainTaskRequestDto createMainTaskRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Long projectId = createMainTaskRequestDto.getProjectId();


        projectMemberPermissionCheck(projectId, userEmail, OWNER);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

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
        Long projectId = createSubTaskRequestDto.getProjectId();
        projectMemberPermissionCheck(projectId,userEmail, OWNER);
        Optional<MainTask> optional = mainTaskRepository.findById(mainTaskId);
        MainTask mainTask = emptyThrowResourceNotFoundException(optional, "Not Found MainTask");

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
        projectMemberPermissionCheck(projectId,userEmail, OWNER);
        Optional<SubTask> optional = subTaskRepository.findById(subTaskId);
        SubTask subTask = emptyThrowResourceNotFoundException(optional, "SubTask Not Found");
        List<String> assignMembers = assignMemberRequestDto.getAssignedMembers();
        createTaskMember(assignMembers, projectId, subTask);
    }

    private boolean isExistAssignMembers(CreateSubTaskRequestDto createSubTaskRequestDto) {
        return !createSubTaskRequestDto.getAssignedMembers().isEmpty();
    }

    /**
     * SubTask 에 할당할 assignedMembers 리스트를 ProjectService 에 조회후 존재할 시 TaskMember 객체 생성
     * @param assignedMembers SubTask 에 할당할 회원 이메일 리스트
     * @param projectId ProjectId
     * @param subTask 할당할 SubTask
     * */

    private void createTaskMember(List<String> assignedMembers, Long projectId, SubTask subTask) {
        assignedMembers.forEach(
                email -> {
                    projectClient.getProjectMember(projectId, email);
                    new TaskMember(email, subTask);
                }
        );
    }

    /**
     * ProjectService 에 ProjectId 에 해당하는 Project 유저의 권한 조회해 권한이 부족할 시 PermissionException 반환
     * @param projectId 권한 조회할 ProjectId
     * @param userEmail 권한 조회할 userEmail
     * @param allowPermission 허용할 권한 Role
     */
    private void projectMemberPermissionCheck(Long projectId, String userEmail, Role...allowPermission){
        ProjectMemberDto projectMember = projectClient.getProjectMember(projectId, userEmail);
        if(Arrays.stream(allowPermission).noneMatch(role -> projectMember.getRole().equals(role.name()))){
            throw new PermissionException("lack of permission");
        }
    }

    /**
     * Optional 객체가 비어있으면 ResourceNotFoundException 던짐
     *
     * @param optional 확인할 Optional 객체
     * @param message 오류 메시지
     */
    private <T> T emptyThrowResourceNotFoundException(Optional<T> optional, String message){
        if(optional.isEmpty()){
            throw new ResourceNotFoundException(message);
        }
        return optional.get();
    }
}
