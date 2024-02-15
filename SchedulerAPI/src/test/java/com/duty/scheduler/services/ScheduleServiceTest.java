package com.duty.scheduler.services;

import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.repository.HolydayRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ScheduleServiceTest {

    @Autowired
    ScheduleService scheduleService;

    @MockBean
    HolydayRepository repository;

    @Test
    void getHolydaysInJanuary2024() {

        LocalDate localDate = LocalDate.of(2024, 1, 1);
        when(repository.findByMonth(localDate)).thenReturn(Stream.of(
                new Holyday(0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)),
                new Holyday(0, LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 1))
            ).collect(Collectors.toList()));

        assertEquals(2, scheduleService.getHolydaysInMonth(localDate).size());
    }
}