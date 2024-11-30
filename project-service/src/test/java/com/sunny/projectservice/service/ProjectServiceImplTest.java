package com.sunny.projectservice.service;

import com.sunny.projectservice.domain.Project;
import com.sunny.projectservice.dto.CreateRequestDto;
import com.sunny.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProjectServiceImplTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectRepository projectRepository;
    @Test
    void createTest(){
        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setProjectName("testProject");
        createRequestDto.setDescription("test");
        projectService.create("AnyString",createRequestDto);


        Optional<Project> findProject = projectRepository.findById(1L);
        assertThat(findProject.get().getId()).isEqualTo(1L);
    }
}