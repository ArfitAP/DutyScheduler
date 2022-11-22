package com.duty.scheduler.DTO;

public class UserRoleDTO {
	String username;

	boolean active;

	public UserRoleDTO(String username, boolean active) {
		super();
		this.username = username;
		this.active = active;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
}
