package com.duty.scheduler.repository;

import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserDutyDTO;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserDuty;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDutyRepository userDutyRepository;

    @Test
    public void givenSchedule_whenGetForMonth_thenSchedule(){

        // Given : Setup object or precondition
        User user = new User("TestUser", "user@test.com", "123");

        userRepository.save(user);

        Schedule schedule1 = new Schedule(user, LocalDate.of(2024, 1, 1)
                                            , LocalDateTime.of(2024, 1, 1, 10, 0), true);
        Schedule schedule2 = new Schedule(user, LocalDate.of(2024, 1, 1)
                , LocalDateTime.of(2024, 1, 1, 12, 0), true);

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        // When : Action of behavious that we are going to test
        ScheduleDTO scheduleJanuary = scheduleRepository.getScheduleForMonth(LocalDate.of(2024, 1, 1));

        // Then : Verify the output
        assertThat(scheduleJanuary.getId()).isGreaterThan(0);
        assertThat(scheduleJanuary.getMonth()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(scheduleJanuary.getGeneratedDateTime()).isEqualTo(LocalDateTime.of(2024, 1, 1, 12, 0));

    }

    @Test
    public void givenSchedules_whenFindByMonth_thenScheduleList(){

        // Given : Setup object or precondition
        User user = new User("TestUser", "user@test.com", "123");

        userRepository.save(user);

        Schedule schedule1 = new Schedule(user, LocalDate.of(2024, 1, 1)
                , LocalDateTime.of(2024, 1, 1, 10, 0), true);
        Schedule schedule2 = new Schedule(user, LocalDate.of(2024, 1, 1)
                , LocalDateTime.of(2024, 1, 1, 12, 0), true);
        Schedule schedule3 = new Schedule(user, LocalDate.of(2024, 2, 1)
                , LocalDateTime.of(2024, 1, 1, 12, 0), true);

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
        scheduleRepository.save(schedule3);

        // When : Action of behavious that we are going to test
        List<Schedule> schedulesJanuary = scheduleRepository.findByMonth(LocalDate.of(2024, 1, 1));
        List<Schedule> schedulesFebruary = scheduleRepository.findByMonth(LocalDate.of(2024, 2, 1));

        // Then : Verify the output
        assertThat(schedulesJanuary).isNotEmpty();
        assertThat(schedulesJanuary.size()).isEqualTo(2);

        assertThat(schedulesFebruary).isNotEmpty();
        assertThat(schedulesFebruary.size()).isEqualTo(1);

    }

    @Test
    public void givenUserDuties_whenGetUserDutiesForSchedule_thenDutyList(){

        // Given : Setup object or precondition
        User user = new User("TestUser", "user@test.com", "123");
        userRepository.save(user);

        Schedule schedule1 = new Schedule(user, LocalDate.of(2024, 1, 1)
                , LocalDateTime.of(2024, 1, 1, 10, 0), true);
        Schedule schedule2 = new Schedule(user, LocalDate.of(2024, 1, 1)
                , LocalDateTime.of(2024, 1, 1, 12, 0), true);

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        UserDuty duty1 = new UserDuty(user, schedule1, LocalDate.of(2024, 1, 1), 8);
        UserDuty duty2 = new UserDuty(user, schedule1, LocalDate.of(2024, 1, 3), 8);
        UserDuty duty3 = new UserDuty(user, schedule1, LocalDate.of(2024, 1, 4), 8);
        UserDuty duty4 = new UserDuty(user, schedule2, LocalDate.of(2024, 1, 6), 8);
        UserDuty duty5 = new UserDuty(user, schedule2, LocalDate.of(2024, 1, 13), 8);

        userDutyRepository.save(duty1);
        userDutyRepository.save(duty2);
        userDutyRepository.save(duty3);
        userDutyRepository.save(duty4);
        userDutyRepository.save(duty5);

        // When : Action of behavious that we are going to test
        List<UserDutyDTO> duties1 = scheduleRepository.getUserDutiesForSchedule(schedule1.getId());
        List<UserDutyDTO> duties2 = scheduleRepository.getUserDutiesForSchedule(schedule2.getId());

        // Then : Verify the output
        assertThat(duties1).isNotEmpty();
        assertThat(duties1)
                .hasSize(3)
                .extracting(UserDutyDTO::getDay)
                .containsExactlyInAnyOrder(LocalDate.of(2024, 1, 1)
                                            ,LocalDate.of(2024, 1, 3)
                                            ,LocalDate.of(2024, 1, 4));

        assertThat(duties2).isNotEmpty();
        assertThat(duties2)
                .hasSize(2)
                .extracting(UserDutyDTO::getDay)
                .containsExactlyInAnyOrder(LocalDate.of(2024, 1, 6)
                        ,LocalDate.of(2024, 1, 13));

    }
}
