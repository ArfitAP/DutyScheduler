package com.duty.scheduler.models;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "schedule")
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="generatedby", nullable=false)
    private User generatedBy;
	
	@NotBlank
	private Date month;
	
	@NotBlank
	private Date generatedDateTime;
	
	@NotBlank
	private Boolean valid;

	@OneToMany(mappedBy="schedule", fetch = FetchType.LAZY)
    private Set<UserDuty> userDuties;
	
	public Schedule(User generatedBy, @NotBlank Date month, @NotBlank Date generatedDateTime, @NotBlank Boolean valid) {
		super();
		this.generatedBy = generatedBy;
		this.month = month;
		this.generatedDateTime = generatedDateTime;
		this.valid = valid;
	}

	public Schedule() {
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

	public Date getGeneratedDateTime() {
		return generatedDateTime;
	}

	public void setGeneratedDateTime(Date generatedDateTime) {
		this.generatedDateTime = generatedDateTime;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Set<UserDuty> getUserDuties() {
		return userDuties;
	}

	public void setUserDuties(Set<UserDuty> userDuties) {
		this.userDuties = userDuties;
	} 
	
	
}
