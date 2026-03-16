package com.duty.scheduler.schedulers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.models.UserApplication;

public interface IScheduler extends Runnable {

	public Schedule generateSchedule(LocalDate month, User generatedBy, Room room, List<UserActive> activeUsers, List<UserApplication> userApplications, List<Holyday> holydays, Map<LocalDate, Integer> dayHoursMap);

}
