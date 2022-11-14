package com.duty.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

}
