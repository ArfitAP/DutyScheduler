package com.duty.scheduler.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "holyday")
public class Holyday {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(columnDefinition = "DATE", name = "HolydayDay")
	private LocalDate day;
	
	@Column(columnDefinition = "DATE", name = "HolydayMonth")
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
