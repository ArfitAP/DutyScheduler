package com.duty.scheduler.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.User;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

	List<Room> findByOwner(User owner);

	@Query("SELECT r FROM Room r JOIN r.members m WHERE m = ?1")
	List<Room> findByMember(User user);

	Optional<Room> findByIdAndOwner(Long id, User owner);
}
