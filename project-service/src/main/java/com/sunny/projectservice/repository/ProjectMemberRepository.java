package com.sunny.projectservice.repository;

import com.sunny.projectservice.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByUserEmail(String userEmail);
    Optional<ProjectMember> findByProjectIdAndUserEmail(Long projectId, String userEmail);
}
