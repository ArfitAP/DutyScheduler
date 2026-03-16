package com.duty.scheduler.DTO;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.User;

public class RoomDetailDTO {
	private Long id;
	private String name;
	private String description;
	private String ownerUsername;
	private Long ownerId;
	private LocalDateTime createdAt;
	private int memberCount;
	private boolean isOwner;
	private List<MemberDTO> members;

	public RoomDetailDTO() {
	}

	public RoomDetailDTO(Room room, boolean isOwner) {
		this.id = room.getId();
		this.name = room.getName();
		this.description = room.getDescription();
		this.ownerUsername = room.getOwner().getUsername();
		this.ownerId = room.getOwner().getId();
		this.createdAt = room.getCreatedAt();
		this.memberCount = room.getMembers().size();
		this.isOwner = isOwner;
		this.members = room.getMembers().stream()
				.map(u -> new MemberDTO(u.getId(), u.getUsername()))
				.toList();
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	@JsonProperty("isOwner")
	public boolean isOwner() {
		return isOwner;
	}

	public void setOwner(boolean isOwner) {
		this.isOwner = isOwner;
	}

	public List<MemberDTO> getMembers() {
		return members;
	}

	public void setMembers(List<MemberDTO> members) {
		this.members = members;
	}

	public static class MemberDTO {
		private Long id;
		private String username;

		public MemberDTO() {
		}

		public MemberDTO(Long id, String username) {
			this.id = id;
			this.username = username;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}
	}
}
