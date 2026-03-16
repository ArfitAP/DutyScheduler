package com.duty.scheduler.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.RoomDayHours;

@Repository
public interface RoomDayHoursRepository extends JpaRepository<RoomDayHours, Integer> {

	List<RoomDayHours> findByRoomAndMonth(Room room, LocalDate month);

	void deleteByRoomAndMonth(Room room, LocalDate month);
}
