package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.repository.ApplicationDayRepository;
import com.duty.scheduler.repository.ApplicationRepository;
import com.duty.scheduler.repository.UserActiveRepository;
import com.duty.scheduler.repository.UserRepository;

@Service 
public class ScheduleService implements IScheduleService {

	@Autowired
	ApplicationRepository applicationRepository;
	
	@Autowired
	ApplicationDayRepository applicationDayRepository;
	
	@Autowired
	UserActiveRepository userActiveRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Override
	public UserApplicationDTO getApplicationsInMonthForUser(Long userId, LocalDate month) {
		boolean userActive = userActiveRepository.existsUserActive(userId, month);
		
		List<UserApplicationDTO> userApplications = applicationRepository.findAllApplicationsInMonthForUser(userId, month);
		
		UserApplicationDTO result;
		if(userApplications.size() > 0) 
		{
			result = userApplications.get(0);
			result.setActive(userActive);
		}
		else 
		{
			result = new UserApplicationDTO(userId, month);
			result.setActive(userActive);
		}
		
		return result;
	}
	
	@Override
	public boolean updateApplicationsInMonthForUser(UserApplicationDTO userApplicationDTO) {
		
		try
		{
			User user = userRepository.getReferenceById(userApplicationDTO.getUser_id());
			
			UserApplication userApplication = new UserApplication(user, userApplicationDTO.getMonth(), userApplicationDTO.getGrouped(), userApplicationDTO.getApplicationDays());
			
			List<UserApplication> oldApplications = applicationRepository.findApplicationsInMonthForUser(userApplication.getUser().getId(), userApplication.getMonth());
			
			for(UserApplication ua : oldApplications)
			{
				applicationDayRepository.deleteAll(ua.getApplicationDays());
			}
			
			applicationRepository.deleteAll(oldApplications);
			
			
			UserApplication newUserApplication = applicationRepository.save(userApplication);
			
			for(ApplicationDay ad : userApplication.getApplicationDays())
			{
				applicationDayRepository.save(new ApplicationDay(newUserApplication, ad.getDay()));
			}
			
			applicationRepository.flush();
			
			return true;
		}
		catch(Exception e)
		{
			return false;
		}		
	}
	
}
