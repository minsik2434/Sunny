package com.sunny.userservice.repository;

import com.sunny.userservice.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);
}
