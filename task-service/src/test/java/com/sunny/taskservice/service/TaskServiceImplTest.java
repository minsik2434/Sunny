package com.sunny.taskservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.domain.SubTask;
import com.sunny.taskservice.dto.AssignMemberRequestDto;
import com.sunny.taskservice.dto.CreateMainTaskRequestDto;
import com.sunny.taskservice.dto.CreateSubTaskRequestDto;
import com.sunny.taskservice.dto.ProjectMemberDto;
import com.sunny.taskservice.exception.PermissionException;
import com.sunny.taskservice.repository.MainTaskRepository;
import com.sunny.taskservice.repository.SubTaskRepository;
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
import java.util.ArrayList;
import java.util.List;
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

    @Autowired
    SubTaskRepository subTaskRepository;


    CreateMainTaskRequestDto createMainTaskRequestDto;

    CreateSubTaskRequestDto createSubTaskRequestDto;

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

        createSubTaskRequestDto = new CreateSubTaskRequestDto(
                1L,
                1L,
                "SubTask1",
                "SubTaskDescription",
                "Waiting",
                new ArrayList<>()
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

    @Test
    @DisplayName("SubTask 저장 테스트")
    @Transactional
    void subTaskSaveTest() throws UnsupportedEncodingException, JsonProcessingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        MainTask mainTask = new MainTask(
                "MainTask",
                "MainTaskDescription",
                LocalDateTime.parse("2024-05-02 12:20:30",formatter),
                LocalDateTime.parse("2024-05-03 12:20:30",formatter),
                1L
        );
        mainTaskRepository.save(mainTask);
        createSubTaskRequestDto.getAssignedMembers().add("testEmail@naver.com");
        createSubTaskRequestDto.getAssignedMembers().add("testEmail2@naver.com");

        String userEmail = "testEmail@naver.com";
        when(jwtUtil.getClaim(anyString())).thenReturn(userEmail);

        ProjectMemberDto projectMemberDto1 = new ProjectMemberDto(
                1L,
                "testEmail@naver.com",
                "OWNER"
        );
        ProjectMemberDto projectMemberDto2 = new ProjectMemberDto(
                1L,
                "testEmail2@naver.com",
                "MEMBER"
        );

        String encodedEmail = URLEncoder.encode(userEmail, "UTF-8");
        String assignMember1 = URLEncoder.encode("testEmail@naver.com", "UTF-8");
        String assignMember2 = URLEncoder.encode("testEmail2@naver.com", "UTF-8");
        String url = "/project/"+createMainTaskRequestDto.getProjectId()+"/members?email=";
        createGetProjectMemberStub(url, encodedEmail, projectMemberDto1);
        createGetProjectMemberStub(url, assignMember1, projectMemberDto1);
        createGetProjectMemberStub(url, assignMember2, projectMemberDto2);

        Long subTaskId = taskService.subTaskSave("testToken", createSubTaskRequestDto);

        Optional<SubTask> savedSubTask = subTaskRepository.findById(subTaskId);
        assertThat(savedSubTask.isPresent()).isTrue();
        assertThat(savedSubTask.get().getTaskMemberList())
                .extracting("email")
                .contains("testEmail@naver.com");
        assertThat(savedSubTask.get().getTaskMemberList())
                .extracting("email")
                .contains("testEmail2@naver.com");
    }

    @Test
    @DisplayName("작업 할당 테스트")
    @Transactional
    void addAssignedMemberTest() throws UnsupportedEncodingException, JsonProcessingException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        MainTask mainTask = new MainTask(
                "MainTask",
                "MainTaskDescription",
                LocalDateTime.parse("2024-05-02 12:20:30",formatter),
                LocalDateTime.parse("2024-05-03 12:20:30",formatter),
                1L
        );
        MainTask savedMainTask = mainTaskRepository.save(mainTask);
        SubTask subTask = new SubTask(
                savedMainTask,
                "SubTask1",
                "SubTask",
                "Waiting"
        );
        SubTask savedSubTask = subTaskRepository.save(subTask);

        List<String> assignedMembers = new ArrayList<>();
        assignedMembers.add("testEmail@naver.com");
        assignedMembers.add("testEmail2@naver.com");
        AssignMemberRequestDto assignMemberRequestDto = new AssignMemberRequestDto(
                1L,
                assignedMembers
        );
        ProjectMemberDto projectMemberDto1 = new ProjectMemberDto(
                1L,
                "testEmail@naver.com",
                "OWNER"
        );

        ProjectMemberDto projectMemberDto2 = new ProjectMemberDto(
                1L,
                "testEmail@naver.com",
                "MEMBER"
        );
        String userEmail = "testEmail@naver.com";
        when(jwtUtil.getClaim(anyString())).thenReturn(userEmail);

        String encodeEmail = URLEncoder.encode(userEmail, "UTF-8");
        String assignMemberEmail = URLEncoder.encode("testEmail2@naver.com", "UTF-8");
        String url = "/project/"+assignMemberRequestDto.getProjectId()+"/members?email=";
        createGetProjectMemberStub(url, encodeEmail, projectMemberDto1);
        createGetProjectMemberStub(url, assignMemberEmail, projectMemberDto2);
        taskService.addAssignMember("testToken", savedSubTask.getId(),assignMemberRequestDto);

        Optional<SubTask> testSubTask = subTaskRepository.findById(savedSubTask.getId());
        assertThat(testSubTask.isPresent()).isTrue();
        assertThat(testSubTask.get().getTaskMemberList())
                .extracting("email")
                .contains("testEmail@naver.com");
        assertThat(testSubTask.get().getTaskMemberList())
                .extracting("email")
                .contains("testEmail2@naver.com");
    }

    private void createGetProjectMemberStub(String url, String email, ProjectMemberDto returnObject) throws JsonProcessingException {
        stubFor(get(url+ email)
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(returnObject))));
    }

}