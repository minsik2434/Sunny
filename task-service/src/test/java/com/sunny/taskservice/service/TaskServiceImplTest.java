package com.sunny.taskservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sunny.taskservice.common.JwtUtil;
import com.sunny.taskservice.domain.MainTask;
import com.sunny.taskservice.domain.Role;
import com.sunny.taskservice.domain.SubTask;
import com.sunny.taskservice.domain.TaskMember;
import com.sunny.taskservice.dto.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.sunny.taskservice.domain.Role.*;
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
    JwtUtil jwtUtil;
    @Autowired
    WireMockServer wireMockServer;
    @Autowired
    TaskService taskService;
    @Autowired
    MainTaskRepository mainTaskRepository;
    @Autowired
    SubTaskRepository subTaskRepository;
    Long testProjectId = 1L;
    String requestUserEmail = "testEmail@naver.com";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    ObjectMapper mapper = new ObjectMapper();
    @BeforeEach
    void initTest(){
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

        //ProjectService 에서 requestUserEmail 로 조회 후 반환 받을 객체
        ProjectMemberDto requestUserProjectMemberDto =
                createProjectMemberReturnObject(testProjectId, requestUserEmail, OWNER);
        //요청 객체
        CreateMainTaskRequestDto requestDto = new CreateMainTaskRequestDto(
                testProjectId,
                "MainTask",
                "this is MainTask",
                "2024-05-02 12:20:30",
                "2024-05-03 12:20:30"
        );

        //Mocking
        when(jwtUtil.getClaim(anyString())).thenReturn(requestUserEmail);
        createProjectMemberStub(testProjectId, requestUserEmail, HttpStatus.OK, requestUserProjectMemberDto);

        //로직 실행
        Long mainTaskId = taskService.mainTaskSave("TestToken", requestDto);

        //검증
        Optional<MainTask> savedMainTask = mainTaskRepository.findById(mainTaskId);

        assertThat(savedMainTask.isPresent()).isTrue();
        assertThat(savedMainTask.get().getName()).isEqualTo(requestDto.getName());
        assertThat(savedMainTask.get().getDescription()).isEqualTo(requestDto.getDescription());

        LocalDateTime startDate = savedMainTask.get().getStartDate();
        LocalDateTime deadline = savedMainTask.get().getDeadline();
        String startDateString = startDate.format(formatter);
        String deadlineString = deadline.format(formatter);

        assertThat(startDateString).isEqualTo(requestDto.getStartDateTime());
        assertThat(deadlineString).isEqualTo(requestDto.getDeadLine());
    }

    @Test
    @DisplayName("MainTask 저장 테스트 - 권한 부족")
    void mainTaskSaveTest_lackOfPermission() throws JsonProcessingException, UnsupportedEncodingException {

        ProjectMemberDto requestUserProjectMemberDto = createProjectMemberReturnObject(testProjectId, requestUserEmail, MEMBER);
        CreateMainTaskRequestDto requestDto = new CreateMainTaskRequestDto(
                testProjectId,
                "MainTask",
                "this is MainTask",
                "2024-05-02 12:20:30",
                "2024-05-03 12:20:30"
        );

        //Mocking
        when(jwtUtil.getClaim(anyString())).thenReturn(requestUserEmail);

        //검증
        createProjectMemberStub(testProjectId, requestUserEmail,HttpStatus.OK, requestUserProjectMemberDto);
        assertThatThrownBy(()-> taskService.mainTaskSave("testToken",requestDto))
                .isInstanceOf(PermissionException.class)
                .message().isEqualTo("lack of permission");
    }

    @Test
    @DisplayName("MainTask 저장 테스트 - 회원 찾지 못함")
    void mainTaskSaveTest_NotFoundUser() throws UnsupportedEncodingException, JsonProcessingException {
        CreateMainTaskRequestDto requestDto = new CreateMainTaskRequestDto(
                testProjectId,
                "MainTask",
                "this is MainTask",
                "2024-05-02 12:20:30",
                "2024-05-03 12:20:30"
        );
        when(jwtUtil.getClaim(anyString())).thenReturn(requestUserEmail);
        FailResponse failResponse = FailResponse.builder()
                        .error("Not Found").build();
        createProjectMemberStub(testProjectId, requestUserEmail,HttpStatus.NOT_FOUND,failResponse);
        assertThatThrownBy(()-> taskService.mainTaskSave("testToken",requestDto))
                .isInstanceOf(FeignException.NotFound.class);
    }

    @Test
    @DisplayName("SubTask 저장 테스트")
    @Transactional
    void subTaskSaveTest() throws UnsupportedEncodingException, JsonProcessingException {
        MainTask mainTask = new MainTask(
                "MainTask",
                "MainTaskDescription",
                LocalDateTime.parse("2024-05-02 12:20:30",formatter),
                LocalDateTime.parse("2024-05-03 12:20:30",formatter),
                testProjectId
        );
        MainTask savedMainTask = mainTaskRepository.save(mainTask);
        String assignedMember = "testEmail2@naver.com";
        List<String> assignedMembers = new ArrayList<>();
        assignedMembers.add(assignedMember);
        CreateSubTaskRequestDto createSubTaskRequestDto = new CreateSubTaskRequestDto(
                testProjectId,
                savedMainTask.getId(),
                "SubTask1",
                "SubTaskDescription",
                "Waiting",
                assignedMembers
        );
        when(jwtUtil.getClaim(anyString())).thenReturn(requestUserEmail);

        ProjectMemberDto requestMemberProjectMemberDto = createProjectMemberReturnObject(testProjectId, requestUserEmail, OWNER);
        ProjectMemberDto assignedMemberProjectMemberDto = createProjectMemberReturnObject(testProjectId, assignedMember, MEMBER);


        createProjectMemberStub(testProjectId, requestUserEmail, HttpStatus.OK, requestMemberProjectMemberDto);
        createProjectMemberStub(testProjectId, assignedMember, HttpStatus.OK, assignedMemberProjectMemberDto);

        Long subTaskId = taskService.subTaskSave("testToken", createSubTaskRequestDto);

        Optional<SubTask> savedSubTask = subTaskRepository.findById(subTaskId);
        assertThat(savedSubTask.isPresent()).isTrue();
        assertThat(savedSubTask.get().getTaskMemberList())
                .extracting("email")
                .contains(assignedMember);
    }

    @Test
    @DisplayName("작업 할당 테스트")
    @Transactional
    void addAssignedMemberTest() throws UnsupportedEncodingException, JsonProcessingException {
        MainTask mainTask = new MainTask(
                "MainTask",
                "MainTaskDescription",
                LocalDateTime.parse("2024-05-02 12:20:30",formatter),
                LocalDateTime.parse("2024-05-03 12:20:30",formatter),
                testProjectId
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
        String assignMember1 = "testEmail2@naver.com";
        String assignMember2 = "testEmail3@naver.com";
        assignedMembers.add(assignMember1);
        assignedMembers.add(assignMember2);
        AssignMemberRequestDto assignMemberRequestDto = new AssignMemberRequestDto(
                testProjectId,
                assignedMembers
        );
        ProjectMemberDto requestUserProjectMemberDto = createProjectMemberReturnObject(testProjectId, requestUserEmail, OWNER);
        ProjectMemberDto assignMember1ProjectMemberDto = createProjectMemberReturnObject(testProjectId, assignMember1, MEMBER);
        ProjectMemberDto assignMember2ProjectMemberDto = createProjectMemberReturnObject(testProjectId, assignMember2, MEMBER);

        when(jwtUtil.getClaim(anyString())).thenReturn(requestUserEmail);

        createProjectMemberStub(testProjectId, requestUserEmail, HttpStatus.OK, requestUserProjectMemberDto);
        createProjectMemberStub(testProjectId, assignMember1, HttpStatus.OK, assignMember1ProjectMemberDto);
        createProjectMemberStub(1L, assignMember2,HttpStatus.OK, assignMember2ProjectMemberDto);
        taskService.addAssignMember("testToken", savedSubTask.getId(),assignMemberRequestDto);

        Optional<SubTask> testSubTask = subTaskRepository.findById(savedSubTask.getId());
        assertThat(testSubTask.isPresent()).isTrue();
        assertThat(testSubTask.get().getTaskMemberList().stream()
                .map(TaskMember::getEmail)
                .collect(Collectors.toList()))
                .containsExactly(assignMember1, assignMember2);

    }

    private void createProjectMemberStub(Long projectId, String email, HttpStatus returnStatus, Object returnObject) throws JsonProcessingException, UnsupportedEncodingException {
        String encodedEmail = URLEncoder.encode(email, "UTF-8");
        stubFor(get("/project/"+ projectId + "/members?email=" + encodedEmail)
                .willReturn(aResponse()
                        .withStatus(returnStatus.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody(mapper.writeValueAsString(returnObject))));
    }

    private ProjectMemberDto createProjectMemberReturnObject(Long projectId, String email, Role role){
        return new ProjectMemberDto(
                projectId,
                email,
                role.name()
        );
    }

}