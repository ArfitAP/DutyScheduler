package com.duty.scheduler.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "application_day")
public class ApplicationDay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne()
    @JoinColumn(name="application_id", nullable=false)
    private UserApplication userApplication;
	
	@Column(columnDefinition = "DATE", name = "ApplicationDay")
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
