package com.example.hotdeal.domain.event.infra;

import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventItemInsertRepository {

    private final JdbcTemplate jdbcTemplate;

    public void insertEventItem(List<EventItem> eventItems, Long eventId) {
        String sql = "INSERT INTO event_items (product_id, discount_price, original_price, product_name, event_id) VALUES (?, ?, ?, ?, ?)";
        
        log.info("EventItem JDBC 벌크 insert 시작 - size: {}", eventItems.size());
        long startTime = System.currentTimeMillis();
        
        jdbcTemplate.batchUpdate(sql, eventItems, 1000, (ps, item) -> {
            ps.setLong(1, item.getProductId());
            ps.setBigDecimal(2, item.getDiscountPrice());
            ps.setBigDecimal(3, item.getOriginalPrice());
            ps.setString(4, item.getProductName());
            ps.setLong(5, eventId);  // 직접 eventId 사용
        });
        
        long endTime = System.currentTimeMillis();
        log.info("EventItem JDBC 벌크 insert 완료 - 총 {}건, 소요시간: {}ms",
                 eventItems.size(), (endTime - startTime));
    }
}
