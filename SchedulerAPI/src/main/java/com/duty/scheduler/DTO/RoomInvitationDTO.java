package com.duty.scheduler.DTO;

import java.time.LocalDateTime;

import com.duty.scheduler.models.RoomInvitation;

public class RoomInvitationDTO {
	private Long id;
	private Long roomId;
	private String roomName;
	private String invitedByUsername;
	private String status;
	private LocalDateTime createdAt;

	public RoomInvitationDTO() {
	}

	public RoomInvitationDTO(RoomInvitation inv) {
		this.id = inv.getId();
		this.roomId = inv.getRoom().getId();
		this.roomName = inv.getRoom().getName();
		this.invitedByUsername = inv.getInvitedBy().getUsername();
		this.status = inv.getStatus().name();
		this.createdAt = inv.getCreatedAt();
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

	public String getInvitedByUsername() {
		return invitedByUsername;
	}

	public void setInvitedByUsername(String invitedByUsername) {
		this.invitedByUsername = invitedByUsername;
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
