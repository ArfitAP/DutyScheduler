package com.duty.scheduler.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.DTO.UserRoleDTO;
import com.duty.scheduler.services.ScheduleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

	@Autowired
	ScheduleService scheduleService;

	@GetMapping("/rooms/{roomId}/userapplications/{userid}/{mon}")
	public ResponseEntity<?> userApplications(@PathVariable Long roomId, @PathVariable Long userid, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getApplicationsInMonthForUser(userid, roomId, month));
	}

	@PostMapping("/rooms/{roomId}/adduserapplications")
	public ResponseEntity<?> addUserApplications(@PathVariable Long roomId, @RequestBody UserApplicationDTO userApplication) {
		userApplication.setRoomId(roomId);
		var res = scheduleService.updateApplicationsInMonthForUser(userApplication);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/rooms/{roomId}/schedule/{mon}")
	public ResponseEntity<?> getSchedule(@PathVariable Long roomId, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getScheduleForRoomAndMonth(roomId, month));
	}

	@GetMapping("/rooms/{roomId}/usersforactivation/{mon}")
	public ResponseEntity<?> getUsersForActivation(@PathVariable Long roomId, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getUsersListForActivation(roomId, month));
	}

	@PostMapping("/rooms/{roomId}/saveUserActivesInMonth/{mon}/{userid}")
	public ResponseEntity<?> saveUserActivesInMonth(@PathVariable Long roomId, @PathVariable String mon, @PathVariable Integer userid, @RequestBody List<ActiveUsersDTO> userActives) {
		LocalDate month = LocalDate.parse(mon);
		var res = scheduleService.updateUserActivesInMonth(roomId, month, userid, userActives);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/getUserRole/{username}")
	public ResponseEntity<?> getUserRole(@PathVariable String username) {
		return ResponseEntity.ok(scheduleService.getUserRole(username));
	}

	@PostMapping("/setUserRole")
	public ResponseEntity<?> setUserRole(@RequestBody UserRoleDTO userRole) {
		var res = scheduleService.setUserRole(userRole);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/rooms/{roomId}/generateSchedule/{mon}/{userid}")
	public ResponseEntity<?> generateSchedule(@PathVariable Long roomId, @PathVariable String mon, @PathVariable Integer userid) {
		LocalDate month = LocalDate.parse(mon);
		if (scheduleService.generateSchedule(roomId, month, userid)) {
			return ResponseEntity.ok(true);
		} else {
			return ResponseEntity.badRequest().body("Server is busy generating schedule or not room owner");
		}
	}

	@GetMapping("/rooms/{roomId}/isServerBusy/{mon}")
	public ResponseEntity<?> isServerBusy(@PathVariable Long roomId, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.isServerBusy(roomId, month));
	}

	@GetMapping("/getHolydaysForMonth/{mon}")
	public ResponseEntity<?> getHolydaysForMonth(@PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getHolydaysInMonth(month));
	}
}
