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
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "application_day")
public class ApplicationDay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="application_id", nullable=false)
    private UserApplication userApplication;
	
	@NotBlank
	@Column(columnDefinition = "DATE")
	private LocalDate day;

	public ApplicationDay(UserApplication userApplication, @NotBlank LocalDate day) {
		super();
		this.userApplication = userApplication;
		this.day = day;
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
	
	
}
