package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.common.client.event.HotDealApiClient;
import com.example.hotdeal.domain.common.client.event.dto.EventProductResponse;
import com.example.hotdeal.domain.common.client.product.ProductApiClient;
import com.example.hotdeal.domain.event.domain.dto.*;
import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import com.example.hotdeal.domain.event.infra.EventItemInsertRepository;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import com.example.hotdeal.domain.event.infra.EventItemRepository;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventItemRepository eventItemRepository;
    private final EventItemInsertRepository eventItemInsertRepository;
    private final EventPublisherAsync eventPublisher;
    private final ProductApiClient productApiClient;
    /**
     * 이벤트 생성
     *
     * @param request 요청값
     */
    @Transactional
    public EventResponse createEvent(EventCrateRequest request) {
        long totalStartTime = System.currentTimeMillis();

        // 1. Event 저장
        long step1Start = System.currentTimeMillis();
        Event event = eventRepository.save(request.toEvent());
        long step1End = System.currentTimeMillis();
        log.info("1단계 - Event 저장 완료: {}ms", (step1End - step1Start));

        // 2. ProductApiClient 호출 (외부 API)
        long step2Start = System.currentTimeMillis();
        List<SearchProductResponse> searchProductResponses = productApiClient.getProducts(request.getProductIds());
        long step2End = System.currentTimeMillis();
        log.info("2단계 - ProductApiClient 호출 완료: {}ms, 조회된 상품 수: {}", (step2End - step2Start), searchProductResponses.size());

        // 3. EventItem 객체 생성
        long step3Start = System.currentTimeMillis();
        List<EventItem> eventItems = searchProductResponses.stream().map(response -> new EventItem(response, event.getEventDiscount(), event.getEventId()))
                .toList();
        long step3End = System.currentTimeMillis();
        log.info("3단계 - EventItem 객체 생성 완료: {}ms", (step3End - step3Start));

        // 4. EventItem 벌크 insert
        long step4Start = System.currentTimeMillis();
        eventItemInsertRepository.insertEventItem(eventItems, event.getEventId());
        long step4End = System.currentTimeMillis();
        log.info("4단계 - EventItem 벌크 insert 완료: {}ms", (step4End - step4Start));

        // 5. Event에 EventItem 리스트 설정 (조회용)
        long step5Start = System.currentTimeMillis();
        event.setProducts(eventItems);
        long step5End = System.currentTimeMillis();
        log.info("5단계 - Event에 EventItem 리스트 설정 완료: {}ms", (step5End - step5Start));

        // 6. WSEventProduct 객체 생성
        long step6Start = System.currentTimeMillis();
        List<WSEventProduct> wsEventProducts = eventItems.stream()
                .map(eventItem ->
                        new WSEventProduct(
                                event.getEventId(),
                                eventItem.getProductId(),
                                event.getEventType(),
                                eventItem.getProductName(),
                                eventItem.getOriginalPrice(),
                                eventItem.getDiscountPrice(),
                                event.getEventDiscount()
                        )
                )
                .toList();
        long step6End = System.currentTimeMillis();
        log.info("6단계 - WSEventProduct 객체 생성 완료: {}ms", (step6End - step6Start));

        long step7Start = System.currentTimeMillis();
        log.info("이벤트 발행 시작 - 총 {}개 이벤트", wsEventProducts.size());
        eventPublisher.publishEvent(wsEventProducts);
        long step7End = System.currentTimeMillis();
        log.info("7단계 - 이벤트 발행 완료: {}ms", (step7End - step7Start));

        long totalEndTime = System.currentTimeMillis();
        log.info("=== createEvent 총 실행시간: {}ms ===", (totalEndTime - totalStartTime));

        return new EventResponse(event);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeLastEvent() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = eventRepository.softDeleteExpiredEvents(now);
        log.info("기한이 지난 이벤트 삭제 총 {}개", deletedCount);
    }

    public List<EventProductResponse> getEvent(List<Long> productIds) {
        // 연관관계 제거로 인해 Event와 EventItem을 별도로 조회
        List<Event> events = eventRepository.findActiveEvents();
        List<EventItem> eventItems = eventItemRepository.findByProductIds(productIds);

        Map<Long, EventProductResponse> bestDealEventsMap = new HashMap<>();

        for (Event event : events) {
            // 해당 이벤트의 EventItem들만 필터링
            List<EventItem> eventEventItems = eventItems.stream()
                    .filter(item -> item.getEventId().equals(event.getEventId()))
                    .toList();

            for (EventItem eventItem : eventEventItems) {
                Long currentProductId = eventItem.getProductId();

                if (productIds.contains(currentProductId)) {
                    EventProductResponse currentEventResponse = new EventProductResponse(event, eventItem);

                    if (bestDealEventsMap.containsKey(currentProductId)) {
                        EventProductResponse existingEventResponse = bestDealEventsMap.get(currentProductId);

                        if (currentEventResponse.getDiscountPrice().compareTo(existingEventResponse.getDiscountPrice()) < 0) {
                            bestDealEventsMap.put(currentProductId, currentEventResponse);
                        } else if (currentEventResponse.getDiscountPrice().compareTo(existingEventResponse.getDiscountPrice()) == 0) {
                            if (currentEventResponse.getEventDiscount().compareTo(existingEventResponse.getEventDiscount()) > 0) {
                                bestDealEventsMap.put(currentProductId, currentEventResponse);
                            }
                        }
                    } else {
                        bestDealEventsMap.put(currentProductId, currentEventResponse);
                    }
                }
            }
        }

        return new ArrayList<>(bestDealEventsMap.values());
    }

}
