package com.duty.scheduler.DTO;

import java.time.LocalDate;

import com.duty.scheduler.models.UserDuty;


public class UserDutyDTO {
	
	private Integer id;
	
    private String username;

	private LocalDate day;
	
	private Integer hours;

	public UserDutyDTO(Integer id, String username, LocalDate day, Integer hours) {
		super();
		this.id = id;
		this.username = username;
		this.day = day;
		this.hours = hours;
	}
	
	public UserDutyDTO(UserDuty userDuty, String username) {
		super();
		this.id = userDuty.getId();
		this.username = username;
		this.day = userDuty.getDay();
		this.hours = userDuty.getHours();
	}

	public UserDutyDTO() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getDay() {
		return day;
	}

	public void setDay(LocalDate day) {
		this.day = day;
	}

	public Integer getHours() {
		return hours;
	}

	public void setHours(Integer hours) {
		this.hours = hours;
	}
	
	
}
