package com.duty.scheduler.models;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Column;
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
@Table(name = "user_applications")
public class UserApplication {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
	
	@Column(columnDefinition = "DATE", name = "UserApplicationMonth")
	private LocalDate month;
	
	private Boolean grouped;
	
	@OneToMany(mappedBy="userApplication", fetch = FetchType.EAGER)
    private Set<ApplicationDay> applicationDays;
	
	public UserApplication(User user, @NotBlank LocalDate month, Boolean grouped, Set<ApplicationDay> applicationDays) {
		super();
		this.user = user;
		this.month = month;
		this.grouped = grouped;
		this.applicationDays = applicationDays;
	}

	public UserApplication() {
		super();
	}

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Boolean getGrouped() {
		return grouped;
	}

	public void setGrouped(Boolean grouped) {
		this.grouped = grouped;
	}

	public Set<ApplicationDay> getApplicationDays() {
		return applicationDays;
	}

	public void setApplicationDays(Set<ApplicationDay> applicationDays) {
		this.applicationDays = applicationDays;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	
	
	
}
