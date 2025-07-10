package com.example.hotdeal.domain.user.auth.application;

import java.time.LocalDateTime;

import com.example.hotdeal.domain.user.auth.domain.TokenBlacklist;
import com.example.hotdeal.domain.user.auth.domain.request.LogoutRequest;
import com.example.hotdeal.domain.user.auth.domain.response.AccessTokenResponse;
import com.example.hotdeal.domain.user.auth.domain.Auth;
import com.example.hotdeal.domain.user.auth.domain.request.ReissueRequest;
import com.example.hotdeal.domain.user.auth.domain.response.TokenResponse;
import com.example.hotdeal.domain.user.auth.infra.RefreshTokenRepository;
import com.example.hotdeal.domain.user.auth.infra.TokenBlacklistRepository;
import com.example.hotdeal.domain.user.auth.security.JwtUtil;
import com.example.hotdeal.domain.user.auth.infra.AuthRepository;
import com.example.hotdeal.domain.user.auth.domain.request.SigninRequest;
import com.example.hotdeal.domain.user.auth.domain.request.SignupRequest;
import com.example.hotdeal.domain.user.auth.domain.response.CreateUserResponse;
import com.example.hotdeal.global.enums.CustomErrorCode;
import com.example.hotdeal.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;

    @Transactional
    public CreateUserResponse signup(SignupRequest signupRequest) {
        Auth auth = signupRequest.toAuth();
        Auth savedAuth = authRepository.save(auth);
        return CreateUserResponse.of(savedAuth.getAuth_id(), savedAuth.getEmail(), savedAuth.getName());
    }

    @Transactional
    public TokenResponse signin(SigninRequest signinRequest) {
        Auth auth = authRepository.findByEmail(signinRequest.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));
		return jwtUtil.createTokens(auth.getAuth_id(), auth.getEmail(), auth.getRole());
    }

    public AccessTokenResponse reissueAccessToken(ReissueRequest request) {

        String refreshToken = request.getRefreshToken();

        //1. 토큰 유효성 검사
        jwtUtil.extractClaims(refreshToken);

        ///2. 토큰에서 ID값 추출
        Long authId = jwtUtil.getUserId(refreshToken);

        String foundToken = refreshTokenRepository.findById(authId)
            .orElseThrow(()-> new CustomException(CustomErrorCode.NOT_FOUND_TOKEN))
            .getRefreshToken();

        if(!refreshToken.equals(foundToken)){
            throw new CustomException(CustomErrorCode.TOKEN_MISMATCH);
        }

        Auth auth = authRepository.findById(authId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_USER));

        return AccessTokenResponse.of(jwtUtil.createAccessToken(auth.getAuth_id(), auth.getEmail(), auth.getRole()));
    }

    public void registerTokenBlacklist(LogoutRequest request) {
        String accessToken = request.getAccessToken();

        //엑세스토큰 에서 ID값 추출
        Long authId = jwtUtil.getUserId(accessToken);

        //리프레시 토큰 삭제 -> 삭제가 안될시 만료된(이미삭제된)거라 상관없음
        if(refreshTokenRepository.existsById(authId)){
            refreshTokenRepository.deleteById(authId);
        }

        //엑세스 토큰의 만료시간
        LocalDateTime accessTokenExpiredAt = jwtUtil.getExpiredAt(accessToken);

        //엑세스 토큰의 만료시간이 남았을경우
        if(!accessTokenExpiredAt.isBefore(LocalDateTime.now())){
            tokenBlacklistRepository.save(new TokenBlacklist(accessToken));
        }
    }
}
