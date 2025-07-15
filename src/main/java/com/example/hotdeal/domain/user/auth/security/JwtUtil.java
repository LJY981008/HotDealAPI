package com.example.hotdeal.domain.user.auth.security;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import com.example.hotdeal.domain.user.auth.domain.RefreshToken;
import com.example.hotdeal.domain.user.auth.domain.response.TokenResponse;
import com.example.hotdeal.domain.user.auth.infra.RefreshTokenRepository;
import com.example.hotdeal.global.constant.TokenInfo;
import com.example.hotdeal.global.enums.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JwtUtil")
@Component
@RequiredArgsConstructor
public class JwtUtil {

	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${jwt.secret.key}")
	private String secretKey;

	@PostConstruct
	public void init() {
		log.info("secretKey raw: {}", secretKey);
		try {
			if (!StringUtils.hasText(secretKey)) {
				throw new IllegalArgumentException("JWT 시크릿 키가 설정되지 않았습니다.");
			}

			byte[] bytes = Base64.getDecoder().decode(secretKey);
			if (bytes.length < 32) {
				throw new IllegalArgumentException("JWT 시크릿 키는 최소 32바이트 이상이어야 합니다.");
			}

			key = Keys.hmacShaKeyFor(bytes);
			log.info(String.valueOf(key));
		} catch (IllegalArgumentException e) {
			log.error("JWT 시크릿 키 초기화 실패: {}", e.getMessage());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JWT 설정 오류");
		}
	}

	public TokenResponse createTokens(Long authId, String email, UserRole userRole) {
		Date now = new Date();
		Date accessExpiry = new Date(now.getTime() + TokenInfo.accessTokenExpireTime);
		Date refreshExpiry = new Date(now.getTime() + TokenInfo.refreshTokenExpireTime);

		String accessToken = Jwts.builder()
			.setSubject(String.valueOf(authId)) //유저 식별자
			.claim("email", email) //이메일
			.claim("userRole", userRole) //유저롤
			.setExpiration(accessExpiry)
			.setIssuedAt(now)
			.signWith(key, signatureAlgorithm)
			.compact();

		String refreshToken = Jwts.builder()
			.setSubject(String.valueOf(authId)) // 리프레시 토큰의 주인 == 유저 식별자
			.claim("type", "refresh") //하나의 claim 넣기
			.setExpiration(refreshExpiry) //리프레시토큰에 TTL 설정이 있기에 생략가능
			.setIssuedAt(now)
			.signWith(key, signatureAlgorithm)
			.compact();

		refreshTokenRepository.save(new RefreshToken(authId, refreshToken));

		return TokenResponse.of(accessToken, refreshToken);
	}

	//엑세스 토큰 재발급을 위한 메서드
	public String createAccessToken(Long authId, String email, UserRole userRole) {
		Date now = new Date();
		Date accessExpiry = new Date(now.getTime() + TokenInfo.accessTokenExpireTime);

		return Jwts.builder()
			.setSubject(String.valueOf(authId))
			.claim("email", email)
			.claim("userRole", userRole)
			.setExpiration(accessExpiry)
			.setIssuedAt(now)
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	public String substringToken(String tokenValue) {
		if (!StringUtils.hasText(tokenValue)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다.");
		}
		if (!tokenValue.startsWith(TokenInfo.tokenPrefix)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 토큰 형식입니다.");
		}
		return tokenValue.substring(TokenInfo.tokenPrefix.length());
	}

	public Claims extractClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다.");
		} catch (SecurityException | MalformedJwtException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.");
		} catch (UnsupportedJwtException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다.");
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
		}
	}

	public Long getUserId(String token) {
		try {
			String userId = extractClaims(token).getSubject();
			return Long.parseLong(userId);
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 사용자 ID 형식입니다.");
		}
	}

	public UserRole getUserRole(String token) {
		try {
			String userRole = extractClaims(token).get("userRole", String.class);
			return UserRole.of(userRole);
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "잘못된 사용자 권한입니다.");
		}
	}

	public String getEmail(String token) {
		String userEmail = extractClaims(token).get("email", String.class);
		if (userEmail == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "이메일 정보가 없습니다.");
		}
		return userEmail;
	}

	public LocalDateTime getExpiredAt(String token) {
		Date expiration = extractClaims(token).getExpiration();
		return expiration
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDateTime();
	}

}