package com.duty.scheduler.models;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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

	@ManyToOne
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@Column(columnDefinition = "DATE", name = "UserActiveMonth")
	private LocalDate month;

	public UserActive(User user, User activatedBy, Room room, LocalDate month) {
		super();
		this.user = user;
		this.activatedBy = activatedBy;
		this.room = room;
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}
}
