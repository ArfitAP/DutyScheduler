package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.DTO.UserRoleDTO;
import com.duty.scheduler.models.Holyday;

public interface IScheduleService {

	UserApplicationDTO getApplicationsInMonthForUser(Long userId, Long roomId, LocalDate month);

	boolean updateApplicationsInMonthForUser(UserApplicationDTO userApplication);

	ScheduleDTO getScheduleForRoomAndMonth(Long roomId, LocalDate month);

	List<ActiveUsersDTO> getUsersListForActivation(Long roomId, LocalDate month);

	boolean updateUserActivesInMonth(Long roomId, LocalDate month, Integer userid, List<ActiveUsersDTO> userActives);

	Optional<UserRoleDTO> getUserRole(String username);

	boolean setUserRole(UserRoleDTO userRole);

	boolean generateSchedule(Long roomId, LocalDate month, Integer userid);

	List<Holyday> getHolydaysInMonth(LocalDate month);

	boolean isServerBusy(Long roomId, LocalDate month);
}
