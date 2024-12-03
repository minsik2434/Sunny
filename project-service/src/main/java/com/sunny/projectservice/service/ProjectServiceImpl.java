package com.sunny.projectservice.service;

import com.sunny.projectservice.common.JwtUtil;
import com.sunny.projectservice.domain.Project;
import com.sunny.projectservice.domain.ProjectMember;
import com.sunny.projectservice.dto.*;
import com.sunny.projectservice.exception.AuthorizationException;
import com.sunny.projectservice.exception.DuplicateResourceException;
import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.repository.ProjectMemberRepository;
import com.sunny.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    @Override
    @Transactional
    public Long create(String accessToken, CreateRequestDto createRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Project project = new Project(createRequestDto);
        new ProjectMember(project, userEmail, "OWNER");
        Project savedProject = projectRepository.save(project);
        return savedProject.getId();
    }

    @Override
    public List<ProjectResponseDto> getProject(String userEmail) {
        List<ProjectMember> prList = projectMemberRepository.findByUserEmail(userEmail);
        return prList.stream().map(projectMember -> new ProjectResponseDto(
                projectMember.getProject().getId(),
                projectMember.getProject().getName(),
                projectMember.getProject().getDescription(),
                projectMember.getRole())).toList();
    }

    @Override
    public void invite(String accessToken, InviteRequestDto inviteRequestDto) {
        String fromInviteEmail = jwtUtil.getClaim(accessToken);
        Optional<ProjectMember> fromInvite =
                projectMemberRepository.findByProjectIdAndUserEmail(inviteRequestDto.getProjectId(), fromInviteEmail);
        if(fromInvite.isEmpty()){
            throw new ResourceNotFoundException("Not Found Member");
        }
        if(!(fromInvite.get().getRole().equals("OWNER") || fromInvite.get().getRole().equals("ADMIN"))){
            throw new AuthorizationException("lack of permission");
        }
        String toInviteEmail = inviteRequestDto.getToInviteEmail();
        Long projectId = inviteRequestDto.getProjectId();
        Optional<ProjectMember> toInvite = projectMemberRepository.findByProjectIdAndUserEmail(projectId, toInviteEmail);
        if (toInvite.isPresent()){
            throw new DuplicateResourceException("already registered member");
        }

        String inviteCode = UUID.randomUUID().toString();
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        log.info("{}",inviteCode);

        vop.set(projectId+":"+toInviteEmail, inviteCode, 600000 ,TimeUnit.MILLISECONDS);
    }

    @Override
    @Transactional
    public AcceptResponseDto acceptInvite(String accessToken, AcceptRequestDto acceptRequestDto) {
        String toInviteEmail = jwtUtil.getClaim(accessToken);
        Long projectId = acceptRequestDto.getProjectId();
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if(optionalProject.isEmpty()){
            throw new ResourceNotFoundException("Not Found Project");
        }
        ValueOperations<String, String> vop = redisTemplate.opsForValue();

        String savedInviteCode = vop.get(projectId + ":" + toInviteEmail);
        if(!(acceptRequestDto.getInviteCode().equals(savedInviteCode))){
            throw new ResourceNotFoundException("invalid inviteCode");
        }

        Project project = optionalProject.get();
        ProjectMember projectMember = new ProjectMember(project, toInviteEmail, "MEMBER");
        ProjectMember joinedMember = projectMemberRepository.save(projectMember);

        AcceptResponseDto acceptResponseDto = new AcceptResponseDto();
        acceptResponseDto.setEmail(joinedMember.getUserEmail());
        acceptResponseDto.setProjectName(project.getName());
        acceptResponseDto.setRole(joinedMember.getRole());
        return acceptResponseDto;
    }

}
