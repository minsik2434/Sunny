package com.sunny.taskservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.PermissionException;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.repository.MainTaskRepository;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "feign-endpoint=http://localhost:${wiremock.server.port}"
})
class TaskServiceImplTest {


    @MockitoBean
    private JwtUtil jwtUtil;
    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    TaskService taskService;

    @Autowired
    MainTaskRepository mainTaskRepository;

    CreateMainTaskRequestDto createMainTaskRequestDto;

    ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    void initTest(){
        createMainTaskRequestDto = new CreateMainTaskRequestDto(
                1L,
                "Test",
                "TestMainTask",
                "2024-12-25 12:20:30",
                "2024-12-30 12:20:30"
        );
        wireMockServer.stop();
        wireMockServer.start();
    }
    @AfterEach
    void afterTest(){
        wireMockServer.resetAll();
    }

    @Test
    @DisplayName("MainTask 저장 테스트")
    @Transactional
    void mainTaskSaveTest() throws JsonProcessingException, UnsupportedEncodingException {
        String userEmail = "testEmail@naver.com";

        ProjectMemberDto projectMemberDto = new ProjectMemberDto(
                1L,
                "testEmail@naveer.com",
                "OWNER"
        );

        when(jwtUtil.getClaim(anyString())).thenReturn(userEmail);
        String encodedEmail = URLEncoder.encode(userEmail, "UTF-8");
        stubFor(get("/project/"+createMainTaskRequestDto.getProjectId()+"/members?email="+encodedEmail)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(projectMemberDto))));

        Long mainTaskId = taskService.mainTaskSave("TestToken", createMainTaskRequestDto);
        Optional<MainTask> savedMainTask = mainTaskRepository.findById(mainTaskId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        assertThat(savedMainTask.isPresent()).isTrue();
        assertThat(savedMainTask.get().getName()).isEqualTo(createMainTaskRequestDto.getName());
        assertThat(savedMainTask.get().getDescription()).isEqualTo(createMainTaskRequestDto.getDescription());

        LocalDateTime startDate = savedMainTask.get().getStartDate();
        LocalDateTime deadline = savedMainTask.get().getDeadline();

        String startDateString = startDate.format(formatter);
        String deadlineString = deadline.format(formatter);

        assertThat(startDateString).isEqualTo(createMainTaskRequestDto.getStartDateTime());
        assertThat(deadlineString).isEqualTo(createMainTaskRequestDto.getDeadLine());
    }

    @Test
    @DisplayName("MainTask 저장 테스트 - 권한 부족")
    void mainTaskSaveTest_lackOfPermission() throws JsonProcessingException, UnsupportedEncodingException {
        String userEmail = "testEmail@naver.com";
        ProjectMemberDto projectMemberDto = new ProjectMemberDto(
                1L,
                "testEmail@naveer.com",
                "MEMBER"
        );

        when(jwtUtil.getClaim(anyString())).thenReturn(userEmail);
        String encodedEmail = URLEncoder.encode(userEmail, "UTF-8");
        stubFor(get("/project/"+createMainTaskRequestDto.getProjectId()+"/members?email="+encodedEmail)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(projectMemberDto))));

        assertThatThrownBy(()-> taskService.mainTaskSave("testToken",createMainTaskRequestDto))
                .isInstanceOf(PermissionException.class)
                .message().isEqualTo("lack of permission");
    }

    @Test
    @DisplayName("MainTask 저장 테스트 - 회원 찾지 못함")
    void mainTaskSaveTest_NotFoundUser() throws UnsupportedEncodingException {

        String userEmail = "testEmail@naver.com";
        when(jwtUtil.getClaim(anyString())).thenReturn(userEmail);
        String encodedEmail = URLEncoder.encode(userEmail, "UTF-8");
        stubFor(get("/project/"+createMainTaskRequestDto.getProjectId()+"/members?email="+encodedEmail)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())));

        assertThatThrownBy(()-> taskService.mainTaskSave("testToken",createMainTaskRequestDto))
                .isInstanceOf(FeignException.NotFound.class);
    }
}