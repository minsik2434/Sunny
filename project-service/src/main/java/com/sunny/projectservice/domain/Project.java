package com.sunny.projectservice.domain;

import com.sunny.projectservice.dto.CreateRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Date createAt;

    @OneToMany(mappedBy = "project", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ProjectMember> projectMembers = new ArrayList<>();

    public Project(CreateRequestDto createRequestDto){
        this.name = createRequestDto.getProjectName();
        this.description = createRequestDto.getDescription();
        this.createAt = new Date(System.currentTimeMillis());
    }
}
