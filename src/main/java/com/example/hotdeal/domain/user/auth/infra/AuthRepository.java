package com.example.hotdeal.domain.user.auth.infra;

import com.example.hotdeal.domain.user.auth.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
}
