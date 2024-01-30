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
@Table(name = "application_day")
public class ApplicationDay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne()
    @JoinColumn(name="application_id", nullable=false)
    private UserApplication userApplication;
	
	@Column(columnDefinition = "DATE")
	private LocalDate day;

	@Column()
	private Integer wantedDay;

	public ApplicationDay(UserApplication userApplication, LocalDate day, Integer wantedDay) {
		super();
		this.userApplication = userApplication;
		this.day = day;
		this.wantedDay = wantedDay;
	}

	public ApplicationDay() {
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

	public Integer getWantedDay()
	{
		return this.wantedDay;
	}

	public void setWantedDay(Integer wantedDay) {
		this.wantedDay = wantedDay;
	}

	/*public UserApplication getUserApplication() {
		return userApplication;
	}

	public void setUserApplication(UserApplication userApplication) {
		this.userApplication = userApplication;
	}
	*/
	
}
