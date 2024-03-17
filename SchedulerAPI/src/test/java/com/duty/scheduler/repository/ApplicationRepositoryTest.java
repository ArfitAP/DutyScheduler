package com.duty.scheduler.repository;

import com.duty.scheduler.DTO.ScheduleDTO;
import com.duty.scheduler.DTO.UserApplicationDTO;
import com.duty.scheduler.DTO.UserDutyDTO;
import com.duty.scheduler.models.ApplicationDay;
import com.duty.scheduler.models.Schedule;
import com.duty.scheduler.models.User;
import com.duty.scheduler.models.UserApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ApplicationRepositoryTest {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenUserApplications_whenFindAllApplicationsInMonthForUser_thenApplicationList(){

        // Given : Setup object or precondition
        User user1 = new User("TestUser1", "user1@test.com", "123");
        User user2 = new User("TestUser2", "user2@test.com", "123");

        userRepository.save(user1);
        userRepository.save(user2);

        UserApplication application1 = new UserApplication(user1, LocalDate.of(2024, 1, 1)
                , false, new HashSet<ApplicationDay>());

        UserApplication application2 = new UserApplication(user2, LocalDate.of(2024, 1, 1)
                , true, new HashSet<ApplicationDay>());

        applicationRepository.save(application1);
        applicationRepository.save(application2);

        // When : Action of behavious that we are going to test
        List<UserApplicationDTO> applicationDTOS = applicationRepository.findAllApplicationsInMonthForUser(user1.getId(),
                                                                            LocalDate.of(2024, 1, 1));

        // Then : Verify the output
        assertThat(applicationDTOS)
                .hasSize(1)
                .extracting(UserApplicationDTO::getUser_id)
                .containsExactlyInAnyOrder(user1.getId());

    }
}
