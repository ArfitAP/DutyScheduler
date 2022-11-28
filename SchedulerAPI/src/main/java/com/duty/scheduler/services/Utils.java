package com.duty.scheduler.services;

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

public class Utils {

	public static Schedule generateSchedule(LocalDate month, User generatedBy, List<UserActive> activeUsers, List<UserApplication> userApplications, List<Holyday> holydays)
	{
		/*for(UserActive ua : activeUsers)
		{
			System.out.println(ua.getMonth());
		}
		
		for(UserApplication ua : userApplications)
		{
			System.out.println(ua.getMonth());
		}*/
		
		Schedule result = new Schedule(generatedBy, month, LocalDateTime.now(), true);
		
		Set<UserDuty> userDuties = new HashSet<UserDuty>();
		
		userDuties.add(new UserDuty(generatedBy, result, month, 8));
		
		result.setUserDuties(userDuties);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
		
		return result;
	}
}
