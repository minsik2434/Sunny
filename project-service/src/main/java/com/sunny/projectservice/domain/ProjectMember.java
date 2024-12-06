package com.sunny.projectservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    private String userEmail;
    private String role;
    public ProjectMember(Project project, String userEmail, String role){
        this.project = project;
        this.userEmail = userEmail;
        this.role = role;
        project.getProjectMembers().add(this);
    }

}
