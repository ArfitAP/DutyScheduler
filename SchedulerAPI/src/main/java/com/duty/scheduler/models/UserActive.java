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
	private Date month;

	public UserActive(User user, User activatedBy, @NotBlank Date month) {
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

	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}
	
	
}
