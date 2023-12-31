package com.fintech.masoori.global.config.jwt;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	protected static final String AUTHORITIES_KEY = "Auth";
	private static final String BEARER_TYPE = "Bearer";
	private static final long ACCESS_TOKEN_EXPIRE_TIME = 24 * 60 * 60 * 1000L; // 30분
	private static final long REFRESH_TOKEN_EXPIRE_TIME = 30 * 24 * 60 * 60 * 1000L; // 한달
	private static final int REFRESH_TOKEN_EXPIRE_TIME_COOKIE = 12 * 30 * 24 * 60 * 60; // 12개월
	private final Key key;

	public JwtTokenProvider(@Value("${jwt.key}") String secret) {
		byte[] keyBytes = Decoders.BASE64.decode(secret);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public static int getRefreshTokenExpireTimeCookie() {
		return REFRESH_TOKEN_EXPIRE_TIME_COOKIE;
	}

	public TokenInfo createToken(Authentication authentication) {
		// 유저의 권한들을 가져옴
		String authorities = authentication.getAuthorities()
		                                   .stream()
		                                   .map(GrantedAuthority::getAuthority)
		                                   .collect(Collectors.joining(","));

		long now = new Date().getTime();

		String accessToken = Jwts.builder()
		                         .setSubject(authentication.getName())
		                         .claim(AUTHORITIES_KEY, authorities)
		                         .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
		                         .signWith(key, SignatureAlgorithm.HS256)
		                         .compact();

		String refreshToken = Jwts.builder()
		                          .setSubject(authentication.getName())
		                          .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
		                          .signWith(key, SignatureAlgorithm.HS256)
		                          .compact();

		// 생성된 토큰을 토큰 dto에 담아 반환
		return TokenInfo.builder()
		                .grantType(BEARER_TYPE)
		                .accessToken(accessToken)
		                .refreshToken(refreshToken)
		                .expireTime(REFRESH_TOKEN_EXPIRE_TIME)
		                .build();
	}

	public TokenInfo createToken(String id, String role) {
		long now = new Date().getTime();

		String accessToken = Jwts.builder()
		                         .setSubject(id)
		                         .claim(AUTHORITIES_KEY, role)
		                         .signWith(key, SignatureAlgorithm.HS256)
		                         .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRE_TIME))
		                         .compact();

		String refreshToken = Jwts.builder()
		                          .setSubject(id)
		                          .signWith(key, SignatureAlgorithm.HS256)
		                          .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
		                          .compact();

		return TokenInfo.builder()
		                .grantType(BEARER_TYPE)
		                .accessToken(accessToken)
		                .refreshToken(refreshToken)
		                .expireTime(REFRESH_TOKEN_EXPIRE_TIME)
		                .build();
	}

	//JWT 토큰을 복호화 => 토큰에 정보를 꺼내는 매서드
	public Authentication getAuthentication(String accessToken) {
		Claims claims = parseClaims(accessToken);

		if (claims.get(AUTHORITIES_KEY) == null) {
			throw new RuntimeException("토큰에 권한이 없습니다.");
		}

		// from Claim 권한 정보 가져오기
		Collection<? extends GrantedAuthority> authorities = Arrays.stream(
			                                                           claims.get(AUTHORITIES_KEY).toString().split(","))
		                                                           .map(SimpleGrantedAuthority::new)
		                                                           .collect(Collectors.toList());

		//UserDetails 객체를 만들어서 Authentication 을 return By security
		UserDetails principal = new User(claims.getSubject(), "", authorities);
		return new UsernamePasswordAuthenticationToken(principal, "", authorities);
	}

	// 토큰을 클레임 형태로 추출함
	public Claims parseClaims(String accessToken) {
		try {
			return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
		} catch (ExpiredJwtException e) {
			return e.getClaims();
		}
	}

	public String validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return "vaild";
		} catch (MalformedJwtException e) {
			log.info("MalformedJwtException");
			return "invalid";
		} catch (ExpiredJwtException e) {
			log.info("ExpiredJwtException");
			return "isExpired";
		} catch (UnsupportedJwtException e) {
			log.info("UnsupportedJwtException");
			return "isUnsupporeted";
		} catch (IllegalArgumentException e) {
			log.info("IllegalArgumentException");
		}
		return "isIllegal";
	}

	public boolean getIsExpired(String accessToken) {
		Date expiration = Jwts.parserBuilder()
		                      .setSigningKey(key)
		                      .build()
		                      .parseClaimsJws(accessToken)
		                      .getBody()
		                      .getExpiration();
		// 현재 시간
		long now = new Date().getTime();
		return (expiration.getTime() - now) > 0;
	}
}
