package com.duty.scheduler.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.UserActive;

@Repository
public interface UserActiveRepository extends JpaRepository<UserActive, Long> {

	@Query("select case when count(uact)> 0 then true else false end from UserActive uact join uact.user u where u.id = ?1 and uact.month = ?2")
	boolean existsUserActive(Long userId, LocalDate month);
}
