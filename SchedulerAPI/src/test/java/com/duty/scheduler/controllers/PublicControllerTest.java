package com.duty.scheduler.controllers;

import com.duty.scheduler.models.Holyday;
import com.duty.scheduler.services.ScheduleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PublicControllerTest {

    @MockBean
    ScheduleService scheduleService;

    @Autowired
    private MockMvc mvc;

    @Test
    public void getHolydaysForMonth() throws Exception
    {
        LocalDate localDate = LocalDate.of(2024, 1, 1);
        when(scheduleService.getHolydaysInMonth(localDate)).thenReturn(Stream.of(
                new Holyday(0, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)),
                new Holyday(0, LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 1))
        ).collect(Collectors.toList()));

        mvc.perform(MockMvcRequestBuilders
                        .get("/api/public/schedule/getHolydaysForMonth/2024-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.[*].day").isNotEmpty());
    }
}
