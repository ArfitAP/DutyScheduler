package com.duty.scheduler.models;

import java.time.LocalDate;
import java.util.Date;

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
@Table(name = "user_active")
public class UserActive {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
	
	@ManyToOne
    @JoinColumn(name="activatedby", nullable=false)
    private User activatedBy;
	
	@NotBlank
	@Column(columnDefinition = "DATE")
	private LocalDate month;

	public UserActive(User user, User activatedBy, @NotBlank LocalDate month) {
		super();
		this.user = user;
		this.activatedBy = activatedBy;
		this.month = month;
	}

	public UserActive() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}
	
	
}
