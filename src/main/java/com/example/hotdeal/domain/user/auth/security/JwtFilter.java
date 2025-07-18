package com.example.hotdeal.domain.user.auth.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.hotdeal.domain.user.auth.domain.AuthUserDto;
import com.example.hotdeal.domain.user.auth.infra.TokenBlacklistRepository;
import com.example.hotdeal.global.enums.UserRole;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final TokenBlacklistRepository tokenBlacklistRepository;

	@Override
	public void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain chain
	) throws IOException, ServletException {
		String bearerJwt = request.getHeader("Authorization");

		if (bearerJwt == null || bearerJwt.isEmpty()) {
			chain.doFilter(request, response);
			return;
		}

		String jwt = jwtUtil.substringToken(bearerJwt);

		try {
			Claims claims = jwtUtil.extractClaims(jwt);
			if (claims == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 토큰입니다.");
				return;
			}

			if (tokenBlacklistRepository.existsById(jwt)) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "블랙리스트에 등록된 토큰입니다.");
				return;
			}

			Long userId = jwtUtil.getUserId(jwt);
			UserRole userRole = jwtUtil.getUserRole(jwt);
			String email = jwtUtil.getEmail(jwt);

			AuthUserDto authUser = new AuthUserDto(userId, email, userRole);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				authUser, "", List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()))
			);

			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error("예상치 못한 예외 발생", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

}