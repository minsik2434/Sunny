package com.sunny.taskservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.taskservice.common.ControllerAdvice;
import com.sunny.taskservice.dto.AssignMemberRequestDto;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.CreateSubTaskRequestDto;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.exception.ServiceUnavailable;
import com.sunny.taskservice.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TaskControllerTest {
    @InjectMocks
    TaskController taskController;
    ObjectMapper mapper = new ObjectMapper();

    @Mock
    TaskService taskService;
    MockMvc mockMvc;
    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(ControllerAdvice.class).build();
    }

    @Test
    @DisplayName("작업 저장 테스트")
    void createMainTaskTest() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()+100);
        String startTime = format.format(start);
        String deadLine = format.format(end);
        when(taskService.mainTaskSave(anyString(),any(CreateMainTaskRequestDto.class))).thenReturn(1L);
        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/mainTask")
                .contentType("application/json")
                .header("Authorization","testToken")
                .content(content))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("메인 작업 저장 테스트-Datetime 포맷 오류")
    void createMainTaskTest_InvalidDateTimeFormat() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()+100);
        String startTime = format.format(start);
        String deadLine = format.format(end);

        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/mainTask")
                        .contentType("application/json")
                        .header("Authorization","testToken")
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("메인 작업 저장 테스트-Datetime 포맷 오류")
    void createMainTaskTest_InvalidStartAndDeadLine() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()-10000);
        String startTime = format.format(start);
        String deadLine = format.format(end);

        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/mainTask")
                        .contentType("application/json")
                        .header("Authorization","testToken")
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"));
    }

    @Test
    @DisplayName("메인 작업 저장 테스트-프로젝트 회원 찾지못함")
    void createMainTaskTest_NotFoundProjectMember() throws Exception {
        doThrow(new ResourceNotFoundException("Cannot find users belonging to project"))
                .when(taskService).mainTaskSave(anyString(), any(CreateMainTaskRequestDto.class));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()+10000);
        String startTime = format.format(start);
        String deadLine = format.format(end);

        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/mainTask")
                .contentType("application/json")
                .header("Authorization", "testToken")
                .content(content))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    @DisplayName("메인 작업 저장 테스트-프로젝트 클라이언트 오류")
    void createMainTaskTest_ServiceUnavailable() throws Exception {
        doThrow(new ServiceUnavailable("Unavailable ProjectService"))
                .when(taskService).mainTaskSave(anyString(),any(CreateMainTaskRequestDto.class));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()+10000);
        String startTime = format.format(start);
        String deadLine = format.format(end);

        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/mainTask")
                        .contentType("application/json")
                        .header("Authorization", "testToken")
                        .content(content))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("Service Unavailable"));
    }

    @Test
    @DisplayName("서브 작업 저장 테스트")
    void createSubTaskTest() throws Exception {
        List<String> memberList = new ArrayList<>();
        memberList.add("testEmail@naver.com");
        memberList.add("testEmail2@naver.com");
        CreateSubTaskRequestDto createSubTaskRequestDto = new CreateSubTaskRequestDto(
                1L,
                1L,
                "SubTask1",
                "it is subtask1",
                "Waiting",
                memberList
        );
        String content = mapper.writeValueAsString(createSubTaskRequestDto);
        mockMvc.perform(post("/subTask")
                .contentType("application/json")
                .header("Authorization", "testToken")
                .content(content))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("작업 할당 테스트")
    void addAssignMemberTest() throws Exception {
        List<String> assignMemberEmails = new ArrayList<>();
        assignMemberEmails.add("testEmail@naver.com");
        assignMemberEmails.add("testEmail2@naver.com");

        AssignMemberRequestDto assignMemberRequestDto = new AssignMemberRequestDto(
                1L,
                assignMemberEmails
        );

        String content = mapper.writeValueAsString(assignMemberRequestDto);
        mockMvc.perform(post("/subTask/1/addMember")
                        .contentType("application/json")
                        .header("Authorization", "testToken")
                        .content(content))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("작업 할당 테스트 - 동일한 이메일 존재시")
    void addAssignMemberTest_DuplicateEmail() throws Exception {
        List<String> assignMemberEmails = new ArrayList<>();
        assignMemberEmails.add("testEmail@naver.com");
        assignMemberEmails.add("testEmail@naver.com");

        AssignMemberRequestDto assignMemberRequestDto = new AssignMemberRequestDto(
                1L,
                assignMemberEmails
        );

        String content = mapper.writeValueAsString(assignMemberRequestDto);
        mockMvc.perform(post("/subTask/1/addMember")
                        .contentType("application/json")
                        .header("Authorization", "testToken")
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("{assignMembers=Elements of this Collection must not be duplicated}"));
    }
}