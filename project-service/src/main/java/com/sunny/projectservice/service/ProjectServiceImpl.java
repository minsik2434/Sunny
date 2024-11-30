package com.sunny.projectservice.service;

import com.sunny.projectservice.common.JwtUtil;
import com.sunny.projectservice.domain.Project;
import com.sunny.projectservice.domain.ProjectMember;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.dto.ProjectResponseDto;
import com.sunny.projectservice.repository.ProjectMemberRepository;
import com.sunny.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final JwtUtil jwtUtil;
    @Override
    @Transactional
    public void create(String accessToken, CreateRequestDto createRequestDto) {
        String userEmail = jwtUtil.getClaim(accessToken);
        Project project = new Project(createRequestDto);
        new ProjectMember(project, userEmail, "OWNER");
        projectRepository.save(project);
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

}
