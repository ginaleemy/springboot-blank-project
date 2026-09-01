package com.java.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.java.entity.User;

public interface  UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);
	Boolean existsByEmail(String email);
	Optional<User> findByUsernameOrEmail(String username, String email);
	Boolean existsByUsername(String username);
}
