package com.sunny.projectservice.repository;

import com.sunny.projectservice.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByUserEmail(String userEmail);
}
