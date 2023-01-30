package com.duty.scheduler.schedulers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.models.UserDuty;
import com.duty.scheduler.repository.ScheduleRepository;
import com.duty.scheduler.repository.UserDutyRepository;
import com.duty.scheduler.services.DBStatus;

public class GeneticAlgorithmScheduler implements IScheduler {

	ScheduleRepository scheduleRepository;
	
	UserDutyRepository userDutyRepository;
	
	LocalDate month;
	
	User generatedBy;
	
	List<UserActive> activeUsers;
	
	List<UserApplication> userApplications; 
	
	List<Holyday> holydays;
	
	DBStatus dbStatus;
		
	public GeneticAlgorithmScheduler(LocalDate month, User generatedBy, List<UserActive> activeUsers,
			List<UserApplication> userApplications, List<Holyday> holydays, ScheduleRepository scheduleRepository, UserDutyRepository userDutyRepository, DBStatus dbStatus) {
		this.month = month;
		this.generatedBy = generatedBy;
		this.activeUsers = activeUsers;
		this.userApplications = userApplications;
		this.holydays = holydays;
		this.scheduleRepository = scheduleRepository;
		this.userDutyRepository = userDutyRepository;
		this.dbStatus = dbStatus;
	}
	
	public void run() {
		
		try
		{
			dbStatus.setBusy(true);
			
			Schedule newSchedule = generateSchedule(month, generatedBy, activeUsers, userApplications, holydays);
			
			scheduleRepository.save(newSchedule);
			for(UserDuty duty : newSchedule.getUserDuties())
			{
				userDutyRepository.save(duty);
			}
			
			scheduleRepository.flush();
			userDutyRepository.flush();		
		}
		catch (Exception e)
		{
			
		}
		
		dbStatus.setBusy(false);
    }

	public Schedule generateSchedule(LocalDate month, User generatedBy, List<UserActive> activeUsers, List<UserApplication> userApplications, List<Holyday> holydays )
	{
		
		Schedule result = new Schedule(generatedBy, month, LocalDateTime.now(), true);
		
		Set<UserDuty> userDuties = new HashSet<UserDuty>();
		
		userDuties.add(new UserDuty(generatedBy, result, month, 8));
		
		result.setUserDuties(userDuties);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {

		}
		
		return result;
	}
	
	
}
