package com.example.hotdeal.domain.event.infra;

import com.example.hotdeal.domain.event.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> {
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Event e SET e.deleted = true " +
            "WHERE e.endEventTime < :nowTime AND e.deleted = false")
    int softDeleteExpiredEvents(@Param("nowTime") LocalDateTime nowTime);
}
