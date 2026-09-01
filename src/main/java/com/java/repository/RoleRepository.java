package com.java.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByName(String name);
}
