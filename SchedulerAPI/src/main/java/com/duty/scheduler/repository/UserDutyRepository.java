package com.duty.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.UserDuty;

@Repository
public interface UserDutyRepository extends JpaRepository<UserDuty, Integer> {

}
