package com.example.hotdeal.domain.user.subscribe.infra;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hotdeal.domain.user.subscribe.domain.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

	Optional<Subscribe> findSubscribeByUserIdAndProductId(Long userId, Long productId);

	List<Subscribe> findAllByProductId(Long productId);

}