package com.example.hotdeal.domain.user.profile.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //읽기 전용 서비스
public class UserService {

}
