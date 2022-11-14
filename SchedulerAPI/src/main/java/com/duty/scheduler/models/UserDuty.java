package com.duty.scheduler.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

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
	
	@NotBlank
	private Date day;

	public UserDuty(User user, Schedule schedule, @NotBlank Date day) {
		super();
		this.user = user;
		this.schedule = schedule;
		this.day = day;
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

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
	}
	
	
}
