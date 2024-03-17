package com.duty.scheduler.services;

import com.duty.scheduler.DTO.ActiveUsersDTO;
import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserActive;
import com.duty.scheduler.repository.HolydayRepository;
import com.duty.scheduler.repository.UserActiveRepository;
import com.duty.scheduler.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @MockBean
    HolydayRepository repository;

    @MockBean
    UserActiveRepository userActiveRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    void getHolydaysInJanuary2024() {

        LocalDate localDate = LocalDate.of(2024, 1, 1);
        when(repository.findByMonth(localDate)).thenReturn(Stream.of(
                new Holyday(0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)),
                new Holyday(0, LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 1))
            ).collect(Collectors.toList()));

        assertEquals(2, scheduleService.getHolydaysInMonth(localDate).size());
    }

    @Test
    void updateUserActivesInJanuary2024() {

        // Arrange
        LocalDate localDate = LocalDate.of(2024, 1, 1);
        Integer userId = 1;
        List<ActiveUsersDTO> userActives = new ArrayList<>();
        userActives.add(new ActiveUsersDTO(1L, "Test", localDate, true));
        userActives.add(new ActiveUsersDTO(2L, "Test", localDate, true));

        // Mocking behavior of userRepository
        User user = new User(); // Assuming you have a User class
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);

        // Mocking behavior of userActiveRepository
        when(userActiveRepository.findByMonth(any(LocalDate.class))).thenReturn(new ArrayList<>());

        // Act
        boolean result = scheduleService.updateUserActivesInMonth(localDate, userId, userActives);

        // Assert
        assertTrue(result);
        verify(userActiveRepository, times(1)).deleteAll(anyList());
        verify(userActiveRepository, times(2)).save(any(UserActive.class));
        verify(userActiveRepository, times(1)).flush();

    }
}