package com.duty.scheduler.DTO;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.UserApplication;


public class UserApplicationDTO {
	private Integer id;
	
	private Boolean active;
	
    private Long user_id;

	private LocalDate month;
	
	private Boolean grouped;
	
    private Set<ApplicationDay> applicationDays;

	public UserApplicationDTO(UserApplication uap, Long user_id) {
		this.id = uap.getId();
		this.user_id =  user_id;
		this.month = uap.getMonth();
		this.grouped = uap.getGrouped();
		this.applicationDays = uap.getApplicationDays();
	}
	
	public UserApplicationDTO(Long userId, LocalDate month) {
		this.id = 0;
		this.user_id = userId;
		this.month = month;
		this.grouped = false;
		this.applicationDays = new HashSet<ApplicationDay>();
		this.active = false;
	}
	

	public UserApplicationDTO() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public Boolean getGrouped() {
		return grouped;
	}

	public void setGrouped(Boolean grouped) {
		this.grouped = grouped;
	}

	public Set<ApplicationDay> getApplicationDays() {
		return applicationDays;
	}

	public void setApplicationDays(Set<ApplicationDay> applicationDays) {
		this.applicationDays = applicationDays;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}
