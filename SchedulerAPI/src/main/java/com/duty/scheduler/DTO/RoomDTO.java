package com.duty.scheduler.DTO;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.duty.scheduler.models.Room;

public class RoomDTO {
	private Long id;
	private String name;
	private String description;
	private String roomCode;
	private String ownerUsername;
	private Long ownerId;
	private int memberCount;
	private LocalDateTime createdAt;
	private boolean isOwner;

	public RoomDTO() {
	}

	public RoomDTO(Room room, boolean isOwner) {
		this.id = room.getId();
		this.name = room.getName();
		this.description = room.getDescription();
		this.roomCode = room.getRoomCode();
		this.ownerUsername = room.getOwner().getUsername();
		this.ownerId = room.getOwner().getId();
		this.memberCount = room.getMembers().size();
		this.createdAt = room.getCreatedAt();
		this.isOwner = isOwner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRoomCode() {
		return roomCode;
	}

	public void setRoomCode(String roomCode) {
		this.roomCode = roomCode;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@JsonProperty("isOwner")
	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}
}
