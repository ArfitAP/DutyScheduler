package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;

public interface IScheduleService {
	
	UserApplicationDTO getApplicationsInMonthForUser(Long userId, LocalDate month);
	
	boolean updateApplicationsInMonthForUser(UserApplicationDTO userApplication);
	
	ScheduleDTO getScheduleForMonth(LocalDate month);
	
	List<ActiveUsersDTO> getUsersListForActivation(LocalDate month);
	
	boolean updateUserActivesInMonth(LocalDate month, Integer userid, List<ActiveUsersDTO> userActives);
}
