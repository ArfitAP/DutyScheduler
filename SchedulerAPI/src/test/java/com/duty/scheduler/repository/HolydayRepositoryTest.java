package com.duty.scheduler.repository;

import com.duty.scheduler.models.Holyday;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class HolydayRepositoryTest {

    @Autowired
    private HolydayRepository holydayRepository;

    @Test
    @DisplayName("JUnit test for get Holyday List")
    public void givenHolydayList_whenFindByMonth_thenHolydayList(){

        // Given : Setup object or precondition
        Holyday holydayOne = new Holyday(1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1));
        Holyday holydayTwo = new Holyday(2, LocalDate.of(2024, 1, 6), LocalDate.of(2024, 1, 1));
        Holyday holydayThree = new Holyday(3, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 1));

        holydayRepository.save(holydayOne);
        holydayRepository.save(holydayTwo);
        holydayRepository.save(holydayThree);

        // When : Action of behavious that we are going to test
        List<Holyday> holydaysJanuary = holydayRepository.findByMonth(LocalDate.of(2024, 1, 1));
        List<Holyday> holydaysMay = holydayRepository.findByMonth(LocalDate.of(2024, 5, 1));

        // Then : Verify the output
        assertThat(holydaysJanuary).isNotEmpty();
        assertThat(holydaysJanuary.size()).isEqualTo(2);

        assertThat(holydaysMay).isNotEmpty();
        assertThat(holydaysMay.size()).isEqualTo(1);
    }
}
