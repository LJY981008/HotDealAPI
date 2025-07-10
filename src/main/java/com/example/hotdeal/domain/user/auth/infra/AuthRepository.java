package com.example.hotdeal.domain.user.auth.infra;

import com.example.hotdeal.domain.user.auth.domain.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmailAndDeletedFalse(String email);
    Optional<Auth> findByAuthIdAndDeletedFalse(Long authId);
}
