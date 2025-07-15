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

    // 연관관계 제거로 인해 단순 Event 조회로 변경
    @Query("SELECT e FROM Event e " +
            "WHERE e.deleted = false")
    List<Event> findActiveEvents();

}
