package com.duty.scheduler.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.duty.scheduler.models.Schedule;

public class ScheduleDTO {
	private Integer id;

    private String generatedByUser;

	private Long roomId;

	private String roomName;

	private LocalDate month;

	private LocalDateTime generatedDateTime;

	private Boolean valid;

    private Set<UserDutyDTO> userDuties;

	public ScheduleDTO(Integer id, String generatedByUser, LocalDate month, LocalDateTime generatedDateTime, Boolean valid,
			Set<UserDutyDTO> userDuties) {
		this.id = id;
		this.generatedByUser = generatedByUser;
		this.month = month;
		this.generatedDateTime = generatedDateTime;
		this.valid = valid;
		this.userDuties = userDuties;
	}

	public ScheduleDTO(Schedule sch, String generatedByUser) {
		this.id = sch.getId();
		this.generatedByUser = generatedByUser;
		this.roomId = sch.getRoom() != null ? sch.getRoom().getId() : null;
		this.roomName = sch.getRoom() != null ? sch.getRoom().getName() : null;
		this.month = sch.getMonth();
		this.generatedDateTime = sch.getGeneratedDateTime();
		this.valid = sch.getValid();
		this.userDuties = new HashSet<UserDutyDTO>();
	}

	public ScheduleDTO() {
		this.id = 0;
		this.generatedByUser = "";
		this.valid = false;
		this.userDuties = new HashSet<UserDutyDTO>();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGeneratedByUser() {
		return generatedByUser;
	}

	public void setGeneratedByUser(String generatedByUser) {
		this.generatedByUser = generatedByUser;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public LocalDateTime getGeneratedDateTime() {
		return generatedDateTime;
	}

	public void setGeneratedDateTime(LocalDateTime generatedDateTime) {
		this.generatedDateTime = generatedDateTime;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Set<UserDutyDTO> getUserDuties() {
		return userDuties;
	}

	public void setUserDuties(Set<UserDutyDTO> userDuties) {
		this.userDuties = userDuties;
	}
}
