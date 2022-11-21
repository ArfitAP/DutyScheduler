package com.duty.scheduler.DTO;

import java.time.LocalDate;

public class ActiveUsersDTO {
	Long userid;
	
	String username;
	
	LocalDate month;
	
	boolean active;

	public ActiveUsersDTO(Long userid, String username, LocalDate month, boolean active) {
		this.userid = userid;
		this.username = username;
		this.month = month;
		this.active = active;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
