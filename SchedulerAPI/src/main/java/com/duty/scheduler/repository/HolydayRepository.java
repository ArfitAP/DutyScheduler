package com.duty.scheduler.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.duty.scheduler.models.Holyday;


@Repository
public interface HolydayRepository  extends JpaRepository<Holyday, Integer> {
	
	List<Holyday> findByMonth(LocalDate month);
	
}
