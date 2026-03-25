package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duty.scheduler.DTO.RoomDTO;
import com.duty.scheduler.DTO.RoomDayHoursDTO;
import com.duty.scheduler.DTO.RoomDetailDTO;
import com.duty.scheduler.DTO.RoomInvitationDTO;
import com.duty.scheduler.DTO.RoomJoinRequestDTO;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.RoomDayHours;
import com.duty.scheduler.models.RoomInvitation;
import com.duty.scheduler.models.RoomInvitation.InvitationStatus;
import com.duty.scheduler.models.RoomJoinRequest;
import com.duty.scheduler.models.RoomJoinRequest.JoinRequestStatus;
import com.duty.scheduler.models.User;
import com.duty.scheduler.repository.RoomDayHoursRepository;
import com.duty.scheduler.repository.RoomInvitationRepository;
import com.duty.scheduler.repository.RoomJoinRequestRepository;
import com.duty.scheduler.repository.RoomRepository;
import com.duty.scheduler.repository.UserRepository;

@Service
public class RoomService implements IRoomService {

	@Autowired
	RoomRepository roomRepository;

	@Autowired
	RoomDayHoursRepository roomDayHoursRepository;

	@Autowired
	RoomInvitationRepository roomInvitationRepository;

	@Autowired
	RoomJoinRequestRepository roomJoinRequestRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	MessageSource messageSource;

	@Override
	public Room createRoom(String name, String description, User owner) {
		Room room = new Room(name, description, owner);
		return roomRepository.save(room);
	}

	@Override
	public Room updateRoom(Long roomId, String name, String description, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			room.setName(name);
			room.setDescription(description);
			return roomRepository.save(room);
		}
		return null;
	}

	@Override
	public boolean deleteRoom(Long roomId, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		if (roomOpt.isPresent()) {
			roomRepository.delete(roomOpt.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean addMember(Long roomId, Long userId, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		Optional<User> userOpt = userRepository.findById(userId);

		if (roomOpt.isPresent() && userOpt.isPresent()) {
			Room room = roomOpt.get();
			room.getMembers().add(userOpt.get());
			roomRepository.save(room);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeMember(Long roomId, Long userId, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);

		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			if (room.getOwner().getId().equals(userId)) {
				return false;
			}
			room.getMembers().removeIf(u -> u.getId().equals(userId));
			roomRepository.save(room);
			return true;
		}
		return false;
	}

	@Override
	public List<RoomDTO> getRoomsForUser(User user) {
		List<RoomDTO> result = new ArrayList<>();

		List<Room> memberRooms = roomRepository.findByMember(user);
		for (Room room : memberRooms) {
			result.add(new RoomDTO(room, room.getOwner().getId().equals(user.getId())));
		}

		return result;
	}

	@Override
	public RoomDetailDTO getRoomDetail(Long roomId, User user) {
		Optional<Room> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			if (room.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()))) {
				return new RoomDetailDTO(room, room.getOwner().getId().equals(user.getId()));
			}
		}
		return null;
	}

	@Override
	@Transactional
	public boolean setDayHours(Long roomId, LocalDate month, List<RoomDayHoursDTO> hours, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		if (roomOpt.isPresent()) {
			Room room = roomOpt.get();
			roomDayHoursRepository.deleteByRoomAndMonth(room, month);

			for (RoomDayHoursDTO dto : hours) {
				if (dto.getHours() != null && dto.getHours() >= 0 && dto.getHours() <= 24) {
					roomDayHoursRepository.save(new RoomDayHours(room, month, dto.getDay(), dto.getHours()));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public List<RoomDayHoursDTO> getDayHours(Long roomId, LocalDate month) {
		Optional<Room> roomOpt = roomRepository.findById(roomId);
		if (roomOpt.isPresent()) {
			return roomDayHoursRepository.findByRoomAndMonth(roomOpt.get(), month)
					.stream()
					.map(RoomDayHoursDTO::new)
					.toList();
		}
		return List.of();
	}

	public boolean inviteUser(Long roomId, Long userId, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		Optional<User> userOpt = userRepository.findById(userId);

		if (roomOpt.isPresent() && userOpt.isPresent()) {
			Room room = roomOpt.get();
			User invitedUser = userOpt.get();

			// Already a member
			if (room.getMembers().stream().anyMatch(m -> m.getId().equals(userId))) {
				return false;
			}

			// Already has pending invitation
			if (roomInvitationRepository.existsByRoomAndInvitedUserAndStatus(room, invitedUser, InvitationStatus.PENDING)) {
				return false;
			}

			roomInvitationRepository.save(new RoomInvitation(room, invitedUser, owner));
			return true;
		}
		return false;
	}

	public List<RoomInvitationDTO> getPendingInvitations(User user) {
		return roomInvitationRepository.findByInvitedUserAndStatus(user, InvitationStatus.PENDING)
				.stream()
				.map(RoomInvitationDTO::new)
				.toList();
	}

	@Transactional
	public boolean respondToInvitation(Long invitationId, boolean accept, User user) {
		Optional<RoomInvitation> invOpt = roomInvitationRepository.findById(invitationId);
		if (invOpt.isPresent()) {
			RoomInvitation inv = invOpt.get();
			if (!inv.getInvitedUser().getId().equals(user.getId())) {
				return false;
			}
			if (inv.getStatus() != InvitationStatus.PENDING) {
				return false;
			}

			if (accept) {
				inv.setStatus(InvitationStatus.ACCEPTED);
				Room room = inv.getRoom();
				room.getMembers().add(user);
				roomRepository.save(room);
				roomJoinRequestRepository.deleteByRoomAndUser(room, user);
			} else {
				inv.setStatus(InvitationStatus.DECLINED);
			}
			roomInvitationRepository.save(inv);
			return true;
		}
		return false;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public List<RoomJoinRequestDTO> getMyJoinRequests(User user) {
		return roomJoinRequestRepository.findByUser(user)
				.stream()
                .filter(req -> req.getStatus() == JoinRequestStatus.PENDING)
				.map(RoomJoinRequestDTO::new)
				.toList();
	}

	public String requestToJoinRoom(String roomCode, User user, Locale locale) {
		Optional<Room> roomOpt = roomRepository.findByRoomCode(roomCode);
		if (roomOpt.isEmpty()) {
			return messageSource.getMessage("error.room.code.not.found", null, locale);
		}

		Room room = roomOpt.get();

		if (room.getMembers().stream().anyMatch(m -> m.getId().equals(user.getId()))) {
			return messageSource.getMessage("error.already.member", null, locale);
		}

		if (roomJoinRequestRepository.existsByRoomAndUserAndStatus(room, user, JoinRequestStatus.PENDING)) {
			return messageSource.getMessage("error.request.already.sent", null, locale);
		}

		roomJoinRequestRepository.save(new RoomJoinRequest(room, user));
		return null;
	}

	public List<RoomJoinRequestDTO> getPendingJoinRequests(Long roomId, User owner) {
		Optional<Room> roomOpt = roomRepository.findByIdAndOwner(roomId, owner);
		if (roomOpt.isEmpty()) {
			return List.of();
		}

		return roomJoinRequestRepository.findByRoomAndStatus(roomOpt.get(), JoinRequestStatus.PENDING)
				.stream()
				.map(RoomJoinRequestDTO::new)
				.toList();
	}

	@Transactional
	public boolean respondToJoinRequest(Long requestId, boolean approve, User owner) {
		Optional<RoomJoinRequest> reqOpt = roomJoinRequestRepository.findById(requestId);
		if (reqOpt.isEmpty()) {
			return false;
		}

		RoomJoinRequest req = reqOpt.get();
		if (!req.getRoom().getOwner().getId().equals(owner.getId())) {
			return false;
		}
		if (req.getStatus() != JoinRequestStatus.PENDING) {
			return false;
		}

		if (approve) {
			req.setStatus(JoinRequestStatus.APPROVED);
			Room room = req.getRoom();
			room.getMembers().add(req.getUser());
			roomRepository.save(room);
			roomInvitationRepository.deleteByRoomAndInvitedUser(room, req.getUser());
		} else {
			req.setStatus(JoinRequestStatus.REJECTED);
		}
		roomJoinRequestRepository.save(req);
		return true;
	}
}
