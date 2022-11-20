package com.duty.scheduler.controllers;

import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.duty.scheduler.DTO.UserApplicationDTO;
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

}
