package com.sunny.taskservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.taskservice.common.ControllerAdvice;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@Slf4j
class TaskControllerTest {

    @InjectMocks
    TaskController taskController;
    ObjectMapper mapper = new ObjectMapper();

    MockMvc mockMvc;

    @BeforeEach
    void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(ControllerAdvice.class).build();
    }

    @Test
    @DisplayName("작업 저장 테스트")
    void createTask() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start = new Date(System.currentTimeMillis());
        Date end = new Date(System.currentTimeMillis()+100);
        String startTime = format.format(start);
        String deadLine = format.format(end);
        CreateMainTaskRequestDto createMainTaskRequestDto = new CreateMainTaskRequestDto(
                "test",
                "testTask",
                startTime,
                deadLine
        );
        String content = mapper.writeValueAsString(createMainTaskRequestDto);
        mockMvc.perform(post("/task")
                .contentType("application/json")
                .header("Authorization","testToken")
                .content(content))
                .andExpect(status().isCreated());
    }
}