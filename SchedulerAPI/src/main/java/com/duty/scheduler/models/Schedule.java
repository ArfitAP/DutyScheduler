package com.duty.scheduler.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
    @JoinColumn(name="generatedby", nullable=false)
    private User generatedBy;

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@Column(columnDefinition = "DATE", name = "ScheduleMonth")
	private LocalDate month;
	
	@Column(columnDefinition = "TIMESTAMP")
	//@Column(columnDefinition = "DATETIME")
	private LocalDateTime generatedDateTime;
	
	private Boolean valid;

	@OneToMany(mappedBy="schedule", fetch = FetchType.LAZY)
    private Set<UserDuty> userDuties;
	
	public Schedule(User generatedBy, Room room, LocalDate month, LocalDateTime generatedDateTime, Boolean valid) {
		super();
		this.generatedBy = generatedBy;
		this.room = room;
		this.month = month;
		this.generatedDateTime = generatedDateTime;
		this.valid = valid;
		this.userDuties = new HashSet<UserDuty>();
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

	public LocalDate getMonth() {
		return month;
	}

	public void setMonth(LocalDate month) {
		this.month = month;
	}

	public LocalDateTime getGeneratedDateTime() {
		return generatedDateTime;
	}

	public void setGeneratedDateTime(LocalDateTime generatedDateTime) {
		this.generatedDateTime = generatedDateTime;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Set<UserDuty> getUserDuties() {
		return userDuties;
	}

	public void setUserDuties(Set<UserDuty> userDuties) {
		this.userDuties = userDuties;
	} 
	
	
}
