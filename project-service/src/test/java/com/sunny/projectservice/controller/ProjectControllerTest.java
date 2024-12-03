package com.sunny.projectservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.projectservice.common.ControllerAdvice;
import com.sunny.projectservice.dto.AcceptRequestDto;
import com.sunny.projectservice.dto.AcceptResponseDto;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.InviteRequestDto;
import com.sunny.projectservice.exception.AuthorizationException;
import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.naming.AuthenticationException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(ControllerAdvice.class).build();
    }


    ObjectMapper mapper = new ObjectMapper();
    @Test
    @DisplayName("프로젝트 생성 테스트")
    void createTest() throws Exception {
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setProjectName("testName");
        createRequestDto.setDescription("testProject");
        String requestBody = mapper.writeValueAsString(createRequestDto);
        when(projectService.create(anyString(),any(CreateRequestDto.class))).thenReturn(1L);

        mockMvc.perform(post("/create")
                .contentType("application/json")
                .header("Authorization", "testToken")
                .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("초대하기 테스트")
    void inviteTest() throws Exception {
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setToInviteEmail("toInviteEmail");
        inviteRequestDto.setProjectId(1L);
        String requestBody = mapper.writeValueAsString(inviteRequestDto);
        doNothing().when(projectService).invite(anyString(), any(InviteRequestDto.class));

        mockMvc.perform(post("/invite")
                .contentType("application/json")
                .content(requestBody)
                .header("Authorization", "testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("초대 코드 발송"));
    }

    @Test
    @DisplayName("초대하기 권한 오류")
    void inviteTest_lackOfPermission() throws Exception {
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setToInviteEmail("toInviteEmail");
        inviteRequestDto.setProjectId(1L);
        String requestBody = mapper.writeValueAsString(inviteRequestDto);
        doThrow(new AuthorizationException("lackOfPermission"))
                .when(projectService).invite(anyString(), any(InviteRequestDto.class));

        mockMvc.perform(post("/invite")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "testToken"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("초대 수락 테스트")
    void acceptInviteTest() throws Exception {
        AcceptRequestDto acceptRequestDto = new AcceptRequestDto();
        acceptRequestDto.setProjectId(1L);
        acceptRequestDto.setInviteCode("inviteCode");
        String requestBody = mapper.writeValueAsString(acceptRequestDto);

        AcceptResponseDto acceptResponseDto = new AcceptResponseDto();
        acceptResponseDto.setProjectName("test");
        acceptResponseDto.setEmail("testEmail@naver.com");
        acceptResponseDto.setRole("MEMBER");

        when(projectService.acceptInvite(anyString(),any(AcceptRequestDto.class))).thenReturn(acceptResponseDto);

        mockMvc.perform(post("/invite/accept")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "testToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectName").value("test"))
                .andExpect(jsonPath("$.email").value("testEmail@naver.com"))
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    @DisplayName("초대 수락 테스트 - 초대 코드가 다른경우")
    void acceptInviteTest_notMatchInviteCode() throws Exception {
        AcceptRequestDto acceptRequestDto = new AcceptRequestDto();
        acceptRequestDto.setProjectId(1L);
        acceptRequestDto.setInviteCode("inviteCode");
        String requestBody = mapper.writeValueAsString(acceptRequestDto);

        doThrow(new ResourceNotFoundException("invalid inviteCode"))
                .when(projectService).acceptInvite(anyString(),any(AcceptRequestDto.class));

        mockMvc.perform(post("/invite/accept")
                        .contentType("application/json")
                        .content(requestBody)
                        .header("Authorization", "testToken"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("invalid inviteCode"))
                .andExpect(jsonPath("$.path").value("/invite/accept"));
    }
}