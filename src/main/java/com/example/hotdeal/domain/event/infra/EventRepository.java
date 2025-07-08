package com.example.hotdeal.domain.event.infra;

import com.example.hotdeal.domain.event.domain.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long> {
}
