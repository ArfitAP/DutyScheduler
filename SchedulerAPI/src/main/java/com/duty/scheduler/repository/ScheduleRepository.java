package com.duty.scheduler.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	@Query(value = "SELECT new com.duty.scheduler.DTO.ScheduleDTO(sch, u.username) FROM Schedule sch JOIN sch.generatedBy u WHERE sch.month = ?1")
	ScheduleDTO getScheduleForMonth(LocalDate month);
}
