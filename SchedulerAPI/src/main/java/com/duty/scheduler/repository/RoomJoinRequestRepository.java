package com.duty.scheduler.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.RoomJoinRequest;
import com.duty.scheduler.models.RoomJoinRequest.JoinRequestStatus;
import com.duty.scheduler.models.User;

@Repository
public interface RoomJoinRequestRepository extends JpaRepository<RoomJoinRequest, Long> {

	List<RoomJoinRequest> findByRoomAndStatus(Room room, JoinRequestStatus status);

	boolean existsByRoomAndUserAndStatus(Room room, User user, JoinRequestStatus status);

	List<RoomJoinRequest> findByUser(User user);

	void deleteByRoomAndUser(Room room, User user);
}
