package com.duty.scheduler.models;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "holyday")
public class Holyday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "DATE")
	private LocalDate day;
	
	@Column(columnDefinition = "DATE")
	private LocalDate month;

	public Holyday(Integer id, LocalDate day, LocalDate month) {
		super();
		this.id = id;
		this.day = day;
		this.month = month;
	}

	public Holyday() {
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

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}
	
	
}
