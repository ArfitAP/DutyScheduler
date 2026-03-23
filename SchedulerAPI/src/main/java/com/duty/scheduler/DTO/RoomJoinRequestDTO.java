package com.duty.scheduler.DTO;

import java.time.LocalDateTime;

import com.duty.scheduler.models.RoomJoinRequest;

public class RoomJoinRequestDTO {
	private Long id;
	private Long roomId;
	private String roomName;
	private Long userId;
	private String username;
	private String status;
	private LocalDateTime createdAt;

	public RoomJoinRequestDTO() {
	}

	public RoomJoinRequestDTO(RoomJoinRequest req) {
		this.id = req.getId();
		this.roomId = req.getRoom().getId();
		this.roomName = req.getRoom().getName();
		this.userId = req.getUser().getId();
		this.username = req.getUser().getUsername();
		this.status = req.getStatus().name();
		this.createdAt = req.getCreatedAt();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRoomId() {
		return roomId;
	}

	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}

	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
