package com.duty.scheduler.services;

import java.time.LocalDate;
import java.util.List;

import com.duty.scheduler.DTO.RoomDTO;
import com.duty.scheduler.DTO.RoomDayHoursDTO;
import com.duty.scheduler.DTO.RoomDetailDTO;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.models.User;

public interface IRoomService {

	Room createRoom(String name, String description, User owner);

	Room updateRoom(Long roomId, String name, String description, User owner);

	boolean deleteRoom(Long roomId, User owner);

	boolean addMember(Long roomId, Long userId, User owner);

	boolean removeMember(Long roomId, Long userId, User owner);

	List<RoomDTO> getRoomsForUser(User user);

	RoomDetailDTO getRoomDetail(Long roomId, User user);

	boolean setDayHours(Long roomId, LocalDate month, List<RoomDayHoursDTO> hours, User owner);

	List<RoomDayHoursDTO> getDayHours(Long roomId, LocalDate month);
}
