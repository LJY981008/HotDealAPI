package com.example.hotdeal.domain.event.application;

import com.example.hotdeal.domain.event.domain.dto.*;
import com.example.hotdeal.domain.event.domain.entity.Event;
import com.example.hotdeal.domain.event.domain.entity.EventItem;
import com.example.hotdeal.domain.event.infra.EventRepository;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductListRequest;
import com.example.hotdeal.domain.product.product.domain.dto.SearchProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final RestTemplate restTemplate;

    /**
     * 이벤트 생성
     *
     * @param request 요청값
     */
    @Transactional
    public EventResponse createEvent(EventCrateRequest request) {
        Event event = eventRepository.save(request.toEvent());
        List<SearchProductResponse> searchProductResponses = productSearch(request.getProductIds());
        List<EventItem> eventItems = searchProductResponses.stream().map(response -> new EventItem(response, event.getEventDiscount(), event))
                .toList();
        event.setProducts(eventItems);

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

        log.info("이벤트 발행 시작 - 총 {}개 이벤트", wsEventProducts.size());
        wsEventProducts.forEach(wsEvent -> {
            log.info("이벤트 발행: {}", wsEvent.product_id());
            eventPublisher.publishEvent(wsEvent);
        });
        log.info("이벤트 발행 완료");

        return new EventResponse(event);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void removeLastEvent() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = eventRepository.softDeleteExpiredEvents(now);
        log.info("기한이 지난 이벤트 삭제 총 {}개", deletedCount);
    }

    /**
     * 레스트 템플릿 호출
     *
     * @param productIds
     * @return
     */
    private List<SearchProductResponse> productSearch(List<Long> productIds) {
        SearchProductListRequest request = new SearchProductListRequest(productIds);
        URI uri = UriComponentsBuilder
                .fromUriString("http://localhost:8080")
                .path("/api/products/search-product")
                .encode()
                .build()
                .toUri();

        ResponseEntity<List<SearchProductResponse>> response = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                new HttpEntity<>(request),
                new ParameterizedTypeReference<List<SearchProductResponse>>() {
                }
        );

        return response.getBody();
    }

    public List<EventProductResponse> getEvent(List<Long> productIds) {
        List<Event> events = eventRepository.findEventsByProductIds(productIds);

        Map<Long, EventProductResponse> bestDealEventsMap = new HashMap<>();

        for (Event event : events) {

            for (EventItem eventItem : event.getProducts()) {
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
