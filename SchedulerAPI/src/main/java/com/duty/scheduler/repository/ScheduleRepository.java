package com.duty.scheduler.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserDutyDTO;
import com.duty.scheduler.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	@Query(value = "SELECT new com.duty.scheduler.DTO.ScheduleDTO(sch, u.username) FROM Schedule sch JOIN sch.generatedBy u WHERE sch.month = ?1 AND sch.valid = true AND sch.generatedDateTime = (SELECT max(s.generatedDateTime) FROM Schedule s WHERE s.month = ?1 AND s.valid = true)")
	ScheduleDTO getScheduleForMonth(LocalDate month);
	
	List<Schedule> findByMonth(LocalDate month);
	
	@Query(value = "SELECT new com.duty.scheduler.DTO.UserDutyDTO(ud, u.username) FROM UserDuty ud JOIN ud.user u JOIN ud.schedule sch WHERE sch.id = ?1")
	List<UserDutyDTO> getUserDutiesForSchedule(Integer scheduleId);
}
