package com.duty.scheduler.models;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_duty")
public class UserDuty {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
	
	@ManyToOne
    @JoinColumn(name="schedule_id", nullable=false)
    private Schedule schedule;
	
	@Column(columnDefinition = "DATE", name = "UserDutyDay")
	private LocalDate day;
	
	@Column()
	private Integer hours;

	public UserDuty(User user, Schedule schedule, LocalDate day, Integer hours) {
		super();
		this.user = user;
		this.schedule = schedule;
		this.day = day;
		this.hours = hours;
	}

	public UserDuty() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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
