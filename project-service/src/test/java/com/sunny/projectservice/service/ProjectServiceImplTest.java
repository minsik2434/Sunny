package com.sunny.projectservice.service;

import com.sunny.projectservice.common.JwtUtil;
import com.sunny.projectservice.domain.Project;
import com.sunny.projectservice.domain.ProjectMember;
import com.sunny.projectservice.dto.AcceptRequestDto;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.InviteRequestDto;
import com.sunny.projectservice.exception.AuthorizationException;
import com.sunny.projectservice.exception.DuplicateResourceException;
import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.repository.ProjectMemberRepository;
import com.sunny.projectservice.repository.ProjectRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@Slf4j
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

    CreateRequestDto createRequestDto;


    @BeforeEach
    void initTest(){
        createRequestDto = new CreateRequestDto();
        createRequestDto.setProjectName("test");
        createRequestDto.setDescription("testDescription");
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
    void inviteTest(){
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setProjectId(savedProject.getId());
        inviteRequestDto.setToInviteEmail("test2Email@naver.com");
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
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @Transactional
    void inviteTest_alreadyRegisteredMember(){
        Project project = new Project(createRequestDto);
        new ProjectMember(project, "testEmail@naver.com", "OWNER");
        Project savedProject = projectRepository.save(project);

        ProjectMember projectMember = new ProjectMember(savedProject, "testEmail2@naver.com", "MEMBER");
        projectMemberRepository.save(projectMember);

        when(jwtUtil.getClaim(anyString())).thenReturn("testEmail@naver.com");
        InviteRequestDto inviteRequestDto = new InviteRequestDto();
        inviteRequestDto.setProjectId(savedProject.getId());
        inviteRequestDto.setToInviteEmail("testEmail2@naver.com");
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

}