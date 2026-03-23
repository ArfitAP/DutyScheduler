package com.duty.scheduler.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duty.scheduler.DTO.RoomDayHoursDTO;
import com.duty.scheduler.DTO.RoomDetailDTO;
import com.duty.scheduler.DTO.RoomInvitationDTO;
import com.duty.scheduler.DTO.RoomJoinRequestDTO;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.User;
import com.duty.scheduler.payload.request.CreateRoomRequest;
import com.duty.scheduler.payload.response.MessageResponse;
import com.duty.scheduler.repository.UserRepository;
import com.duty.scheduler.security.services.UserDetailsImpl;
import com.duty.scheduler.services.RoomService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/rooms")
public class RoomController {

	@Autowired
	RoomService roomService;

	@Autowired
	UserRepository userRepository;

	@PostMapping
	public ResponseEntity<?> createRoom(@RequestBody CreateRoomRequest request) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		Room room = roomService.createRoom(request.getName(), request.getDescription(), owner);
		return ResponseEntity.ok(new RoomDetailDTO(room, true));
	}

	@PutMapping("/{roomId}")
	public ResponseEntity<?> updateRoom(@PathVariable Long roomId, @RequestBody CreateRoomRequest request) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		Room room = roomService.updateRoom(roomId, request.getName(), request.getDescription(), owner);
		if (room == null) return ResponseEntity.badRequest().body("Room not found or not owner");
		return ResponseEntity.ok(new RoomDetailDTO(room, true));
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<?> deleteRoom(@PathVariable Long roomId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean deleted = roomService.deleteRoom(roomId, owner);
		if (!deleted) return ResponseEntity.badRequest().body("Room not found or not owner");
		return ResponseEntity.ok(true);
	}

	@GetMapping("/my")
	public ResponseEntity<?> getMyRooms() {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		return ResponseEntity.ok(roomService.getRoomsForUser(user));
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<?> getRoomDetail(@PathVariable Long roomId) {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		RoomDetailDTO detail = roomService.getRoomDetail(roomId, user);
		if (detail == null) return ResponseEntity.badRequest().body("Room not found or not a member");
		return ResponseEntity.ok(detail);
	}

	@PostMapping("/{roomId}/members/{userId}")
	public ResponseEntity<?> addMember(@PathVariable Long roomId, @PathVariable Long userId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean added = roomService.addMember(roomId, userId, owner);
		if (!added) return ResponseEntity.badRequest().body("Failed to add member. Room not found, user not found, or not owner");
		return ResponseEntity.ok(true);
	}

	@DeleteMapping("/{roomId}/members/{userId}")
	public ResponseEntity<?> removeMember(@PathVariable Long roomId, @PathVariable Long userId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean removed = roomService.removeMember(roomId, userId, owner);
		if (!removed) return ResponseEntity.badRequest().body("Failed to remove member. Room not found, not owner, or cannot remove owner");
		return ResponseEntity.ok(true);
	}

	@GetMapping("/{roomId}/day-hours/{mon}")
	public ResponseEntity<?> getDayHours(@PathVariable Long roomId, @PathVariable String mon) {
		LocalDate month = LocalDate.parse(mon);
		return ResponseEntity.ok(roomService.getDayHours(roomId, month));
	}

	@PostMapping("/{roomId}/day-hours/{mon}")
	public ResponseEntity<?> setDayHours(@PathVariable Long roomId, @PathVariable String mon, @RequestBody List<RoomDayHoursDTO> hours) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		LocalDate month = LocalDate.parse(mon);
		boolean set = roomService.setDayHours(roomId, month, hours, owner);
		if (!set) return ResponseEntity.badRequest().body("Room not found or not owner");
		return ResponseEntity.ok(true);
	}

	@PostMapping("/{roomId}/invite/{userId}")
	public ResponseEntity<?> inviteUser(@PathVariable Long roomId, @PathVariable Long userId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean invited = roomService.inviteUser(roomId, userId, owner);
		if (!invited) return ResponseEntity.badRequest().body("Failed to invite user. Room not found, user not found, already a member, or already invited");
		return ResponseEntity.ok(true);
	}

	@GetMapping("/invitations")
	public ResponseEntity<?> getPendingInvitations() {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		return ResponseEntity.ok(roomService.getPendingInvitations(user));
	}

	@PostMapping("/invitations/{invitationId}/accept")
	public ResponseEntity<?> acceptInvitation(@PathVariable Long invitationId) {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		boolean accepted = roomService.respondToInvitation(invitationId, true, user);
		if (!accepted) return ResponseEntity.badRequest().body("Invitation not found or already responded");
		return ResponseEntity.ok(true);
	}

	@PostMapping("/invitations/{invitationId}/decline")
	public ResponseEntity<?> declineInvitation(@PathVariable Long invitationId) {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		boolean declined = roomService.respondToInvitation(invitationId, false, user);
		if (!declined) return ResponseEntity.badRequest().body("Invitation not found or already responded");
		return ResponseEntity.ok(true);
	}

	@PostMapping("/join-request")
	public ResponseEntity<?> requestToJoin(@RequestBody java.util.Map<String, String> body) {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body(new MessageResponse("User not found"));

		String roomCode = body.get("roomCode");
		if (roomCode == null || roomCode.isBlank()) {
			return ResponseEntity.badRequest().body(new MessageResponse("Identifikator sobe je obavezan."));
		}

		String error = roomService.requestToJoinRoom(roomCode.trim(), user);
		if (error != null) {
			return ResponseEntity.badRequest().body(new MessageResponse(error));
		}
		return ResponseEntity.ok(new MessageResponse("Zahtjev za pridruživanje poslan!"));
	}

	@GetMapping("/my-join-requests")
	public ResponseEntity<?> getMyJoinRequests() {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		return ResponseEntity.ok(roomService.getMyJoinRequests(user));
	}

	@GetMapping("/{roomId}/join-requests")
	public ResponseEntity<?> getPendingJoinRequests(@PathVariable Long roomId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		List<RoomJoinRequestDTO> requests = roomService.getPendingJoinRequests(roomId, owner);
		return ResponseEntity.ok(requests);
	}

	@PostMapping("/join-requests/{requestId}/approve")
	public ResponseEntity<?> approveJoinRequest(@PathVariable Long requestId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean approved = roomService.respondToJoinRequest(requestId, true, owner);
		if (!approved) return ResponseEntity.badRequest().body("Request not found or not authorized");
		return ResponseEntity.ok(true);
	}

	@PostMapping("/join-requests/{requestId}/reject")
	public ResponseEntity<?> rejectJoinRequest(@PathVariable Long requestId) {
		User owner = getCurrentUser();
		if (owner == null) return ResponseEntity.badRequest().body("User not found");

		boolean rejected = roomService.respondToJoinRequest(requestId, false, owner);
		if (!rejected) return ResponseEntity.badRequest().body("Request not found or not authorized");
		return ResponseEntity.ok(true);
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers() {
		User user = getCurrentUser();
		if (user == null) return ResponseEntity.badRequest().body("User not found");

		return ResponseEntity.ok(roomService.getAllUsers().stream()
				.map(u -> new java.util.HashMap<String, Object>() {{
					put("id", u.getId());
					put("username", u.getUsername());
				}})
				.toList());
	}

	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		return userRepository.findById(userDetails.getId()).orElse(null);
	}
}
