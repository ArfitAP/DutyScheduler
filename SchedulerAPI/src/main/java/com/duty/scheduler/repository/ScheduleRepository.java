package com.duty.scheduler.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserDutyDTO;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.Schedule;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	@Query(value = "SELECT new com.duty.scheduler.DTO.ScheduleDTO(sch, u.username) FROM Schedule sch JOIN sch.generatedBy u WHERE sch.room = ?1 AND sch.month = ?2 AND sch.valid = true AND sch.generatedDateTime = (SELECT max(s.generatedDateTime) FROM Schedule s WHERE s.room = ?1 AND s.month = ?2 AND s.valid = true)")
	ScheduleDTO getScheduleForRoomAndMonth(Room room, LocalDate month);

	List<Schedule> findByRoomAndMonth(Room room, LocalDate month);

	@Query(value = "SELECT new com.duty.scheduler.DTO.UserDutyDTO(ud, u.username) FROM UserDuty ud JOIN ud.user u JOIN ud.schedule sch WHERE sch.id = ?1")
	List<UserDutyDTO> getUserDutiesForSchedule(Integer scheduleId);
}
