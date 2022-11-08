package com.duty.scheduler.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.duty.scheduler.models.ERole;
import com.duty.scheduler.models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}

