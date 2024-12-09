package com.sunny.taskservice.repository;

import com.sunny.taskservice.domain.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubTaskRepository extends JpaRepository<SubTask, Long> {
}
