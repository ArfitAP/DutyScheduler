package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.repository.ApplicationDayRepository;
import com.duty.scheduler.repository.ApplicationRepository;
import com.duty.scheduler.repository.ScheduleRepository;
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
	
	@Autowired
	ScheduleRepository scheduleRepository;
	
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

	@Override
	public ScheduleDTO getScheduleForMonth(LocalDate month) {
		ScheduleDTO schedule = scheduleRepository.getScheduleForMonth(month);
		if(schedule == null) schedule = new ScheduleDTO();
		
		return schedule;
	}

	@Override
	public List<ActiveUsersDTO> getUsersListForActivation(LocalDate month) {

		List<ActiveUsersDTO> res = new ArrayList<ActiveUsersDTO>();
		
		List<User> users = userRepository.findAll();
		
		for(User user : users)
		{
			boolean userActive = userActiveRepository.existsUserActive(user.getId(), month);
			
			res.add(new ActiveUsersDTO(user.getId(), user.getUsername(), month, userActive));
		}
		
		return res;
	}

	@Override
	public boolean updateUserActivesInMonth(LocalDate month, Integer userid, List<ActiveUsersDTO> userActives) {
		try
		{
			List<UserActive> oldActives = userActiveRepository.findByMonth(month);	
			
			userActiveRepository.deleteAll(oldActives);
				
			for(ActiveUsersDTO au : userActives)
			{
				if(au.isActive())
				{
					User user = userRepository.getReferenceById(au.getUserid());
					User activatedBy = userRepository.getReferenceById(Long.parseLong(userid.toString()));
					
					UserActive userActive = new UserActive(user, activatedBy, month);
									
					userActiveRepository.save(userActive);
				}
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
