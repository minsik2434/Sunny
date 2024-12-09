package com.sunny.projectservice.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sunny.projectservice.common.JwtUtil;
import com.sunny.projectservice.domain.Project;
import com.sunny.projectservice.domain.ProjectMember;
import com.sunny.projectservice.dto.AcceptRequestDto;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.InviteRequestDto;
import com.sunny.projectservice.dto.ProjectMemberDto;
import com.sunny.projectservice.exception.PermissionException;
import com.sunny.projectservice.exception.DuplicateResourceException;
import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.repository.ProjectMemberRepository;
import com.sunny.projectservice.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {
        "feign-endpoint=http://localhost:${wiremock.server.port}"
})
class ProjectServiceImplTest extends TestContainerConfig{

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;
    @Autowired
    ProjectMemberRepository projectMemberRepository;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @MockitoBean
    JwtUtil jwtUtil;


    @Autowired
    WireMockServer wireMockServer;

    CreateRequestDto createRequestDto;


    @BeforeEach
    void initTest(){
        wireMockServer.stop();
        wireMockServer.start();
        createRequestDto = new CreateRequestDto();
        createRequestDto.setProjectName("test");
        createRequestDto.setDescription("testDescription");
    }

    @AfterEach
    void afterTest(){
        wireMockServer.resetAll();
    }

    @Test
    @Transactional
    void createTest(){
        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        Long savedId = projectService.create("testToken", createRequestDto);
        Project project = projectRepository.findById(savedId).get();
        assertThat(project.getId()).isEqualTo(savedId);
        assertThat(project.getName()).isEqualTo("test");
        assertThat(project.getDescription()).isEqualTo("testDescription");
        assertThat(project.getProjectMembers().get(0).getUserEmail()).isEqualTo("testEmail@naver.com");
    }

    @Test
    @Transactional
    void inviteTest() throws UnsupportedEncodingException {
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setProjectId(savedProject.getId());
        inviteRequestDto.setToInviteEmail("test2Email@naver.com");
        String encodedEmail = URLEncoder.encode("test2Email@naver.com", "UTF-8");


        stubFor(get(urlEqualTo("/user/"+encodedEmail+"/exist"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                ));

        projectService.invite("testToken", inviteRequestDto);

        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String inviteCode = vop.get(savedProject.getId()+":test2Email@naver.com");

        assertThat(inviteCode).isNotNull();
    }

    @Test
    @Transactional
    void inviteTest_lackOfPermission(){
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "MEMBER");
        Project savedProject = projectRepository.save(project);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setProjectId(savedProject.getId());
        inviteRequestDto.setToInviteEmail("test2Email@naver.com");

        assertThatThrownBy(()-> projectService.invite("testToken", inviteRequestDto))
                .isInstanceOf(PermissionException.class);
    }

    @Test
    @Transactional
    void inviteTest_alreadyRegisteredMember() throws UnsupportedEncodingException {
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember(savedProject, "testEmail2@naver.com", "MEMBER");
        projectMemberRepository.save(projectMember);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setProjectId(savedProject.getId());
        inviteRequestDto.setToInviteEmail("testEmail2@naver.com");

        String encodedEmail = URLEncoder.encode("testEmail2@naver.com", "UTF-8");
        stubFor(get(urlEqualTo("/user/"+encodedEmail+"/exist"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                ));


        assertThatThrownBy(()-> projectService.invite("testToken", inviteRequestDto))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @Transactional
    void acceptInviteTest(){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        vop.set(savedProject.getId()+":testEmail2@naver.com","inviteCode",600000,
                TimeUnit.MILLISECONDS);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail2@naver.com");
        AcceptRequestDto acceptRequestDto = new AcceptRequestDto();
        acceptRequestDto.setInviteCode("inviteCode");
        acceptRequestDto.setProjectId(savedProject.getId());

        projectService.acceptInvite("accessToken", acceptRequestDto);


        ProjectMember invitedMember =
                projectMemberRepository.findByProjectIdAndUserEmail(savedProject.getId(),
                        "testEmail2@naver.com").get();

        assertThat(project.getProjectMembers()).contains(invitedMember);
    }

    @Test
    @Transactional
    void acceptInviteTest_notMatchInviteCode(){
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        vop.set(savedProject.getId()+":testEmail2@naver.com","inviteCode",600000,
                TimeUnit.MILLISECONDS);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail2@naver.com");
        AcceptRequestDto acceptRequestDto = new AcceptRequestDto();
        acceptRequestDto.setInviteCode("NotMatchInviteCode");
        acceptRequestDto.setProjectId(savedProject.getId());

        assertThatThrownBy(()->projectService.acceptInvite("accessToken", acceptRequestDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("프로젝트 멤버 조회")
    void getProjectMemberTest(){
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project save = projectRepository.save(project);

        ProjectMemberDto projectMember = projectService.getProjectMember(save.getId(), "testEmail@naver.com");

        assertThat(projectMember.getProjectId()).isEqualTo(save.getId());
        assertThat(projectMember.getUserEmail()).isEqualTo("testEmail@naver.com");
        assertThat(projectMember.getRole()).isEqualTo("OWNER");
    }

}