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
@RequestMapping("/api/test/schedule")
public class ScheduleController {
	
	@Autowired
	ScheduleService scheduleService;
	
	@GetMapping("/userapplications/{userid}/{mon}")
	public ResponseEntity<?> userApplications(@PathVariable Long userid, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getApplicationsInMonthForUser(userid, month));
	}
	
	@PostMapping("/adduserapplications")
	public ResponseEntity<?> addUserApplications(@RequestBody UserApplicationDTO userApplication) {
				
		var res = scheduleService.updateApplicationsInMonthForUser(userApplication);
		
		return ResponseEntity.ok(res);
	}
	
	@GetMapping("/schedule/{mon}")
	public ResponseEntity<?> getSchedule(@PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getScheduleForMonth(month));
	}
	
	@GetMapping("/usersforactivation/{mon}")
	public ResponseEntity<?> getUsersForActivation(@PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getUsersListForActivation(month));
	}
	
	@PostMapping("/saveUserActivesInMonth/{mon}/{userid}")
	public ResponseEntity<?> saveUserActivesInMonth(@PathVariable String mon, @PathVariable Integer userid, @RequestBody List<ActiveUsersDTO> userActives) {
		LocalDate month = LocalDate.parse(mon);
		
		var res = scheduleService.updateUserActivesInMonth(month, userid, userActives);
		
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
	
	@GetMapping("/generateSchedule/{mon}/{userid}")
	public ResponseEntity<?> generateSchedule(@PathVariable String mon, @PathVariable Integer userid) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.generateSchedule(month, userid));
	}
	
	

}
