package com.duty.scheduler.controllers;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duty.scheduler.services.ScheduleService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/public/schedule")
public class PublicController {
	
	@Autowired
	ScheduleService scheduleService;
	
	@GetMapping("/schedule/{mon}")
	public ResponseEntity<?> getSchedule(@PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getScheduleForMonth(month));
	}
	
	@GetMapping("/getHolydaysForMonth/{mon}")
	public ResponseEntity<?> getHolydaysForMonth(@PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(scheduleService.getHolydaysInMonth(month));
	}
}
