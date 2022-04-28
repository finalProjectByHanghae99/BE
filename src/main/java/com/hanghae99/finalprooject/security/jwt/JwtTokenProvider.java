package com.hanghae99.finalprooject.security.jwt;

import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

  @Value("${jwt.secret-key}")
  private String secretKey;

  // Access Token 유효기간 - 1시간
  private static final Long acessTokenValidTime = 60 * 60 * 1000L;

  // Refresh Token 유효기간 - 7일
  private static final Long refreshTokenValidTime = 7 * 24 * 60 * 60 * 1000L;

  private final UserDetailsService userDetailsService;

  @PostConstruct
  protected void init() {
    secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
  }

  // 토큰 생성
  public TokenDto createToken(String userPk, String email) {

    Claims claims = Jwts.claims().setSubject(userPk);
    claims.put("username", email);


    Date now = new Date();

    String accessToken = Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + acessTokenValidTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();

    String refreshToken = Jwts.builder()
        .setIssuedAt(now)
        .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
        .signWith(SignatureAlgorithm.HS256, secretKey)
        .compact();

    return TokenDto.builder()
        .accessToken(accessToken)
        .accessTokenExpiresIn(acessTokenValidTime)
        .refreshToken(refreshToken)
        .build();
  }

  // 토큰에서 회원 정보 추출
  public String getUserPk(String token) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
  }

  // JWT 토큰에서 인증 정보 조회
  public Authentication getAuthentication(String token) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public String resolveToken(HttpServletRequest request) {
    return request.getHeader("Authorization");
  }

  // 토큰의 유효성 + 만료일자 확인
  public JwtReturn validateToken(String jwtToken) {
    System.out.println(jwtToken);
    try {
      Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
      if (claims.getBody().getExpiration().after(new Date())) {
        return JwtReturn.SUCCESS;
      } else {
        return JwtReturn.FAIL;
      }
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다");
      throw new PrivateException(ErrorCode.JWT_TOKEN_EXPIRED);
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다");
      throw new PrivateException(ErrorCode.JWT_TOKEN_NOT_SUPPORTED);
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다");
      throw new PrivateException(ErrorCode.JWT_TOKEN_WRONG_FORM);
    } catch (MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다");
      throw new PrivateException(ErrorCode.JWT_TOKEN_WRONG_SIGNATURE);
    } catch (Exception e) {
      log.info(e.getMessage());
    }
    return JwtReturn.FAIL;
  }

  public String getAccessTokenPayload(String accessToken) {
    return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(accessToken)
        .getBody().getSubject();
  }
}