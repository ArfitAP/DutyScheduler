package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.repository.ApplicationRepository;

@Service 
public class ScheduleService implements IScheduleService {

	@Autowired
	ApplicationRepository applicationRepository;
	
	@Override
	public List<UserApplication> getApplicationsInMonthForUser(Long userId, LocalDate month) {
		return applicationRepository.findAllApplicationsInMonthForUser(userId, month);
	}
	
}
