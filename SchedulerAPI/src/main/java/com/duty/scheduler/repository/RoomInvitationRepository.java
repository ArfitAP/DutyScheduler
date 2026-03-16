package com.duty.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.RoomInvitation;
import com.duty.scheduler.models.RoomInvitation.InvitationStatus;
import com.duty.scheduler.models.User;

@Repository
public interface RoomInvitationRepository extends JpaRepository<RoomInvitation, Long> {

	List<RoomInvitation> findByInvitedUserAndStatus(User invitedUser, InvitationStatus status);

	Optional<RoomInvitation> findByRoomAndInvitedUserAndStatus(Room room, User invitedUser, InvitationStatus status);

	boolean existsByRoomAndInvitedUserAndStatus(Room room, User invitedUser, InvitationStatus status);
}
