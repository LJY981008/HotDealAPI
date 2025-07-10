package com.example.hotdeal.domain.user.subscribe.application;

import com.example.hotdeal.domain.user.subscribe.domain.Subscribe;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;
import com.example.hotdeal.domain.user.subscribe.infra.SubscribeRepository;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;

    @Transactional
    public List<SubscribeResponse> subscribeProduct(Long userId, List<Long> productIds) {
        List<Subscribe> subscribes = productIds.stream().map(pId -> new Subscribe(userId, pId)).toList();
        List<Subscribe> savedSubscribes = subscribeRepository.saveAll(subscribes);

        return savedSubscribes.stream().map(sub -> new SubscribeResponse(userId, sub.getProductId())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubscribeResponse> getSubscribeUserByProductId(Long productId) {
        List<Subscribe> subscribes = subscribeRepository.findAllByProductId(productId);
        if (subscribes.isEmpty()) {
            throw new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT, "제품을 구독한 사용자가 없습니다.");
        }

        return subscribes.stream().map(subscribe -> new SubscribeResponse(subscribe.getUserId(), subscribe.getProductId())).collect(Collectors.toList());
    }

    @Transactional
    public void cancelSubscribe(Long userId, Long productId) {
        Subscribe subscribe = subscribeRepository.findSubscribeByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PRODUCT, "구독한 제품이 아닙니다."));
        subscribeRepository.delete(subscribe);
    }

}
