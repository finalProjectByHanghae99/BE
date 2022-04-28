package com.hanghae99.finalprooject.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = jwtTokenProvider.resolveToken(request);

    if (token != null && !token.isEmpty()) {
      token = token.replaceAll("Bearer", "");
    }

    if (token != null && jwtTokenProvider.validateToken(token) == JwtReturn.EXPIRED) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401

      return;
    }
    if (token != null && jwtTokenProvider.validateToken(token) == JwtReturn.FAIL) {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN); //403

      return;
    }

    if (token != null && jwtTokenProvider.validateToken(token) == JwtReturn.SUCCESS) {
      Authentication authentication = jwtTokenProvider.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);

  }
}
