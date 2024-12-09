package com.sunny.taskservice.repository;

import com.sunny.taskservice.domain.MainTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MainTaskRepository extends JpaRepository<MainTask, Long> {
}
