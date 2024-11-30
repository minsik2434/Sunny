package com.sunny.projectservice.repository;

import com.sunny.projectservice.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
