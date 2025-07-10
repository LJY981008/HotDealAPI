package com.example.hotdeal.domain.user.auth.infra;

import com.example.hotdeal.domain.user.auth.domain.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Auth, Long> {

    boolean existsByEmail(String email);

    Optional<Auth> findByEmailAndDeletedFalse(String email);
    Optional<Auth> findByAuthIdAndDeletedFalse(Long authId);

    //복구전용 메서드 삭제 상태인 Auth 반환
    Optional<Auth> findByAuthIdAndDeletedTrue(Long authId);

    @Modifying(clearAutomatically = true)
    @Query("update Auth a set a.deleted = false where a.authId = :authId")
    void restoredByAuthId(@Param("authId") Long authId);

}
