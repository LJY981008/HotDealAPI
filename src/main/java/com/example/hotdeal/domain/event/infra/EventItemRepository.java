package com.example.hotdeal.domain.event.infra;

import com.example.hotdeal.domain.event.domain.entity.EventItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventItemRepository extends JpaRepository<EventItem, Long> {

    List<EventItem> findByEventId(Long eventId);

    @Query("SELECT ei FROM EventItem ei WHERE ei.productId IN :productIds")
    List<EventItem> findByProductIds(@Param("productIds") List<Long> productIds);

    @Query("SELECT ei FROM EventItem ei WHERE ei.eventId = :eventId AND ei.productId IN :productIds")
    List<EventItem> findByEventIdAndProductIds(@Param("eventId") Long eventId, @Param("productIds") List<Long> productIds);
} 