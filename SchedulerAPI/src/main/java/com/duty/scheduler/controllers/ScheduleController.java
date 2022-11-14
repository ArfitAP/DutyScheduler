package com.duty.scheduler.controllers;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duty.scheduler.repository.UserRepository;
import com.duty.scheduler.services.ScheduleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test/schedule")
public class ScheduleController {
	
	@Autowired
	ScheduleService scheduleService;
	
	@GetMapping("/userapplications/{userid}/{start}/{end}")
	public ResponseEntity<?> userApplications(@PathVariable Long userid, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getUserApplicationsInMonth(userid, month));
	}

}
