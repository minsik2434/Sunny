package com.sunny.projectservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @InjectMocks
    ProjectController projectController;
    @Mock
    ProjectService projectService;

    MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(projectController).build();
    }


    ObjectMapper mapper = new ObjectMapper();
    @Test
    @DisplayName("프로젝트 생성 테스트")
    void createTest() throws Exception {
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setProjectName("testName");
        createRequestDto.setDescription("testProject");
        String requestBody = mapper.writeValueAsString(createRequestDto);
        doNothing().when(projectService).create(anyString(), any(CreateRequestDto.class));

        mockMvc.perform(post("/create")
                .contentType("application/json")
                .header("Authorization", "testToken")
                .content(requestBody))
                .andExpect(status().isCreated());
    }
}