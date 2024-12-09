package com.sunny.taskservice.controller;


import com.sunny.taskservice.exception.ServiceUnavailable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.taskservice.common.ControllerAdvice;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.service.TaskService;
import com.sunny.taskservice.exception.ResourceNotFoundException;
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
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
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
    void createTaskTest() throws Exception {
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
    @DisplayName("작업 저장 테스트-Datetime 포맷 오류")
    void createTaskTest_InvalidDateTimeFormat() throws Exception {
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
    @DisplayName("작업 저장 테스트-Datetime 포맷 오류")
    void createTaskTest_InvalidStartAndDeadLine() throws Exception {
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
    @DisplayName("작업 저장 테스트-프로젝트 회원 찾지못함")
    void createTaskTest_NotFoundProjectMember() throws Exception {
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
    @DisplayName("작업 저장 테스트-프로젝트 클라이언트 오류")
    void createTaskTest_ServiceUnavailable() throws Exception {
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
}