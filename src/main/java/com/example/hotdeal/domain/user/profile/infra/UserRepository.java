package com.example.hotdeal.domain.user.profile.infra;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotdeal.domain.user.profile.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserId(Long userId);

	void deleteByUserId(Long userId);

}