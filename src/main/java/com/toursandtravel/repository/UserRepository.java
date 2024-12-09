package com.toursandtravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.toursandtravel.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUsername(String username);
	User findByEmail(String email);
}
