package com.example.hotdeal.domain.user.auth.infra;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.example.hotdeal.domain.user.auth.domain.TokenBlacklist;

@Repository
public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
}