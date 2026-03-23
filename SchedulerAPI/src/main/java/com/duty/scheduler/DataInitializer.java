package com.duty.scheduler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.duty.scheduler.models.ERole;
import com.duty.scheduler.models.Role;
import com.duty.scheduler.models.Room;
import com.duty.scheduler.repository.RoleRepository;
import com.duty.scheduler.repository.RoomRepository;

@Configuration
@Profile("!test")
public class DataInitializer {

	@Bean
	CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			for (ERole eRole : ERole.values()) {
				if (roleRepository.findByName(eRole).isEmpty()) {
					roleRepository.save(new Role(eRole));
				}
			}
		};
	}

	@Bean
	CommandLineRunner backfillRoomCodes(RoomRepository roomRepository) {
		return args -> {
			for (Room room : roomRepository.findAll()) {
				if (room.getRoomCode() == null || room.getRoomCode().isBlank()) {
					room.setRoomCode(Room.generateCode());
					roomRepository.save(room);
				}
			}
		};
	}
}
