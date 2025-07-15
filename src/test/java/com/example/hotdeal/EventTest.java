package com.example.hotdeal;
import com.example.hotdeal.domain.common.client.product.ProductApiClient;
import com.example.hotdeal.domain.common.client.product.dto.SearchProductResponse;
import com.example.hotdeal.domain.event.domain.dto.EventCrateRequest;
import com.example.hotdeal.domain.event.enums.EventType;
import com.example.hotdeal.domain.notification.infra.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.hotdeal.domain.product.domain.ProductCategory;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EventTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private ProductApiClient productApiClient;

    @BeforeEach
    void setUp() {
        List<SearchProductResponse> mockProducts = new ArrayList<>();
        ProductCategory[] categories = ProductCategory.values();
        
        // 미리 계산된 값들을 준비
        BigDecimal basePrice = BigDecimal.valueOf(10000);
        
        for (long i = 1; i <= 10000; i++) {
            // BigDecimal 계산을 최소화
            BigDecimal price = basePrice.add(BigDecimal.valueOf(i));
            
            mockProducts.add(new SearchProductResponse(
                i, // productId
                "테스트상품" + i, // productName
                price, // originalPrice (미리 계산)
                "설명" + i, // productDescription
                "https://example.com/image" + i + ".jpg", // productImageUri
                categories[(int)(i % categories.length)] // productCategory
            ));
        }
        when(productApiClient.getProducts(anyList()))
                .thenReturn(mockProducts);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void 이벤트_생성시_알림_10000개_벌크인서트_테스트() throws Exception {
        // given: 10,000개 상품 ID 등 이벤트 생성 요청 데이터 준비
        List<Long> productIds = new ArrayList<>();
        for (long i = 1; i <= 10000; i++) {
            productIds.add(i);
        }
        EventCrateRequest request = new EventCrateRequest(
                EventType.HOT_DEAL, BigDecimal.TEN, 7, LocalDateTime.now(), productIds// 필요한 필드 채우기
        );

        // when: 이벤트 생성 API 호출
        mockMvc.perform(post("/api/event/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // then: 알림이 10,000개 생성됐는지 확인 (비동기 벌크 인서트 대기)
        int retry = 0;
        long count = 0;
        do {
            Thread.sleep(1000); // 1초 대기
            count = notificationRepository.count();
            retry++;
        } while (count < 10000 && retry < 10);

        assertEquals(10000, count, "알림이 10,000개 생성되어야 합니다.");
    }
}

