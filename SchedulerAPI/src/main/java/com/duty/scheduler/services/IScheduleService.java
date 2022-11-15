package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;

import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.models.UserApplication;

public interface IScheduleService {
	
	List<UserApplicationDTO> getApplicationsInMonthForUser(Long userId, LocalDate month);
	
}
