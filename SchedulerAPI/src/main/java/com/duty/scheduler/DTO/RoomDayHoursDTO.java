package com.duty.scheduler.DTO;

import java.time.LocalDate;

import com.duty.scheduler.models.RoomDayHours;

public class RoomDayHoursDTO {
	private LocalDate day;
	private Integer hours;

	public RoomDayHoursDTO() {
	}

	public RoomDayHoursDTO(LocalDate day, Integer hours) {
		this.day = day;
		this.hours = hours;
	}

	public RoomDayHoursDTO(RoomDayHours entity) {
		this.day = entity.getDay();
		this.hours = entity.getHours();
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
