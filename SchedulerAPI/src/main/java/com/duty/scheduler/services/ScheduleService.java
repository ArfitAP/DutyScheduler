package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.DTO.UserRoleDTO;
import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.ERole;
import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.models.Role;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.RoomDayHours;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.models.UserApplication;
import com.duty.scheduler.repository.ApplicationDayRepository;
import com.duty.scheduler.repository.ApplicationRepository;
import com.duty.scheduler.repository.HolydayRepository;
import com.duty.scheduler.repository.RoleRepository;
import com.duty.scheduler.repository.RoomDayHoursRepository;
import com.duty.scheduler.repository.RoomRepository;
import com.duty.scheduler.repository.ScheduleRepository;
import com.duty.scheduler.repository.UserActiveRepository;
import com.duty.scheduler.repository.UserDutyRepository;
import com.duty.scheduler.repository.UserRepository;
import com.duty.scheduler.schedulers.GeneticAlgorithmScheduler;
import com.duty.scheduler.schedulers.IScheduler;

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
	RoleRepository roleRepository;

	@Autowired
	ScheduleRepository scheduleRepository;

	@Autowired
	UserDutyRepository userDutyRepository;

	@Autowired
	HolydayRepository holydayRepository;

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	RoomDayHoursRepository roomDayHoursRepository;

	@Autowired
	DBStatus dbStatus;

	@Override
	public UserApplicationDTO getApplicationsInMonthForUser(Long userId, Long roomId, LocalDate month) {
		Optional<Room> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isEmpty()) return new UserApplicationDTO(userId, roomId, month);

		Room room = roomOpt.get();
		boolean userActive = userActiveRepository.existsUserActiveInRoom(userId, room, month);

		List<UserApplicationDTO> userApplications = applicationRepository.findAllApplicationsInMonthForUserAndRoom(userId, room, month);

		UserApplicationDTO result;
		if (userApplications.size() > 0) {
			result = userApplications.get(0);
			result.setActive(userActive);
		} else {
			result = new UserApplicationDTO(userId, roomId, month);
			result.setActive(userActive);
		}

		return result;
	}

	@Override
	public boolean updateApplicationsInMonthForUser(UserApplicationDTO userApplicationDTO) {
		try {
			User user = userRepository.getReferenceById(userApplicationDTO.getUser_id());
			Room room = roomRepository.getReferenceById(userApplicationDTO.getRoomId());

			UserApplication userApplication = new UserApplication(user, room, userApplicationDTO.getMonth(), userApplicationDTO.getGrouped(), userApplicationDTO.getApplicationDays());

			List<UserApplication> oldApplications = applicationRepository.findApplicationsInMonthForUserAndRoom(userApplication.getUser().getId(), room, userApplication.getMonth());

			for (UserApplication ua : oldApplications) {
				applicationDayRepository.deleteAll(ua.getApplicationDays());
			}

			applicationRepository.deleteAll(oldApplications);

			UserApplication newUserApplication = applicationRepository.save(userApplication);

			for (ApplicationDay ad : userApplication.getApplicationDays()) {
				applicationDayRepository.save(new ApplicationDay(newUserApplication, ad.getDay(), ad.getWantedDay()));
			}

			applicationRepository.flush();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public ScheduleDTO getScheduleForRoomAndMonth(Long roomId, LocalDate month) {
		Optional<Room> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isEmpty()) return new ScheduleDTO();

		ScheduleDTO schedule = scheduleRepository.getScheduleForRoomAndMonth(roomOpt.get(), month);
		if (schedule == null) schedule = new ScheduleDTO();
		else schedule.setUserDuties(new HashSet<>(scheduleRepository.getUserDutiesForSchedule(schedule.getId())));

		return schedule;
	}

	@Override
	public List<ActiveUsersDTO> getUsersListForActivation(Long roomId, LocalDate month) {
		List<ActiveUsersDTO> res = new ArrayList<ActiveUsersDTO>();

		Optional<Room> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isEmpty()) return res;

		Room room = roomOpt.get();

		for (User user : room.getMembers()) {
			boolean userActive = userActiveRepository.existsUserActiveInRoom(user.getId(), room, month);
			res.add(new ActiveUsersDTO(user.getId(), user.getUsername(), month, userActive));
		}

		return res;
	}

	@Override
	public boolean updateUserActivesInMonth(Long roomId, LocalDate month, Integer userid, List<ActiveUsersDTO> userActives) {
		try {
			Optional<Room> roomOpt = roomRepository.findById(roomId);
			if (roomOpt.isEmpty()) return false;

			Room room = roomOpt.get();

			List<UserActive> oldActives = userActiveRepository.findByRoomAndMonth(room, month);

			userActiveRepository.deleteAll(oldActives);

			for (ActiveUsersDTO au : userActives) {
				if (au.isActive()) {
					User user = userRepository.getReferenceById(au.getUserid());
					User activatedBy = userRepository.getReferenceById(Long.parseLong(userid.toString()));

					UserActive userActive = new UserActive(user, activatedBy, room, month);

					userActiveRepository.save(userActive);
				}
			}

			userActiveRepository.flush();

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public Optional<UserRoleDTO> getUserRole(String username) {
		Optional<User> user = userRepository.findByUsername(username);
		Optional<Role> adminrole = roleRepository.findByName(ERole.ROLE_ADMIN);

		if (user.isPresent()) {
			if (user.get().getRoles().contains(adminrole.get())) return Optional.of(new UserRoleDTO(username, true));
			else return Optional.of(new UserRoleDTO(username, false));
		}
		return null;
	}

	@Override
	public boolean setUserRole(UserRoleDTO userRole) {
		Optional<User> user = userRepository.findByUsername(userRole.getUsername());
		Optional<Role> adminrole = roleRepository.findByName(ERole.ROLE_ADMIN);
		Optional<Role> userrole = roleRepository.findByName(ERole.ROLE_USER);

		if (user.isPresent()) {
			User us = user.get();
			if (userRole.isActive()) {
				if (us.getRoles().contains(adminrole.get()) == false) {
					Set<Role> newroles = new HashSet<Role>();
					newroles.add(adminrole.get());
					newroles.add(userrole.get());
					us.setRoles(newroles);
				}
			} else {
				if (us.getRoles().contains(adminrole.get()) == true) {
					Set<Role> newroles = new HashSet<Role>();
					newroles.add(userrole.get());
					us.setRoles(newroles);
				}
			}

			userRepository.save(us);
			userRepository.flush();
			return true;
		}

		return false;
	}

	@Override
	public boolean generateSchedule(Long roomId, LocalDate month, Integer userid) {
		try {
			Optional<Room> roomOpt = roomRepository.findById(roomId);
			if (roomOpt.isEmpty()) return false;

			Room room = roomOpt.get();
			User generatedBy = userRepository.getReferenceById(Long.parseLong(userid.toString()));

			// Only room owner can generate schedule
			if (!room.getOwner().getId().equals(generatedBy.getId())) {
				return false;
			}

			List<Schedule> schedules = scheduleRepository.findByRoomAndMonth(room, month);

			for (Schedule sch : schedules) {
				sch.setValid(false);
			}
			scheduleRepository.flush();

			List<UserActive> activeUsers = userActiveRepository.findByRoomAndMonth(room, month);
			List<UserApplication> userApplications = applicationRepository.findByRoomAndMonth(room, month);
			List<Holyday> holydays = holydayRepository.findByMonth(month);

			// Build day hours map from room-specific settings
			Map<LocalDate, Integer> dayHoursMap = new HashMap<>();
			List<RoomDayHours> roomDayHours = roomDayHoursRepository.findByRoomAndMonth(room, month);
			for (RoomDayHours rdh : roomDayHours) {
				dayHoursMap.put(rdh.getDay(), rdh.getHours());
			}

			if (dbStatus.isBusy(roomId, month.toString())) {
				return false;
			} else {
				IScheduler r = new GeneticAlgorithmScheduler(month, generatedBy, room, activeUsers, userApplications, holydays, dayHoursMap, scheduleRepository, userDutyRepository, dbStatus);
				new Thread(r).start();
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<Holyday> getHolydaysInMonth(LocalDate month) {
		return holydayRepository.findByMonth(month);
	}

	@Override
	public boolean isServerBusy(Long roomId, LocalDate month) {
		try {
			return dbStatus.isBusy(roomId, month.toString());
		} catch (Exception e) {
			return true;
		}
	}
}
