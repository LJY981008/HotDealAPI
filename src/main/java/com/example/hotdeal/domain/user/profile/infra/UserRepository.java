package com.example.hotdeal.domain.user.profile.infra;

import com.example.hotdeal.domain.user.profile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
