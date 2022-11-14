package com.duty.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.duty.scheduler.models.UserActive;

@Repository
public interface UserActiveRepository extends JpaRepository<UserActive, Long> {

}
