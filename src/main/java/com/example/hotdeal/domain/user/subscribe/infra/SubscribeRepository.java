package com.example.hotdeal.domain.user.subscribe.infra;

import com.example.hotdeal.domain.user.subscribe.domain.Subscribe;
import com.example.hotdeal.domain.user.subscribe.domain.SubscribeResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe,Long> {
    Optional<Subscribe> findSubscribeByUserIdAndProductId(Long userId, Long productId);

    List<Subscribe> findAllByProductId(Long productId);
}
