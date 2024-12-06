package com.sunny.alarmservice.repository;

import com.sunny.alarmservice.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
