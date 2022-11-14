package com.duty.scheduler.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.UserApplication;

@Repository
public interface ApplicationRepository extends JpaRepository<UserApplication, Long> {

	@Query(value = "SELECT ua FROM UserApplication ua JOIN ua.user u WHERE u.id = ?1 AND ua.month = ?2")
	List<UserApplication> findAllApplicationsInMonthForUser(Long userId, LocalDate month);
	
}
