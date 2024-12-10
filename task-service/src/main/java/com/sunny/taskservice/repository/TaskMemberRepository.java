package com.sunny.taskservice.repository;

import com.sunny.taskservice.domain.TaskMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskMemberRepository extends JpaRepository<TaskMember, Long> {
}
