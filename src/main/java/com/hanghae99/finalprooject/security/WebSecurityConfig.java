package com.hanghae99.finalprooject.security;

import com.hanghae99.finalprooject.security.jwt.JwtAuthenticationFilter;
import com.hanghae99.finalprooject.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private final JwtTokenProvider jwtTokenProvider;

  @Bean
  public BCryptPasswordEncoder encodePassword() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(WebSecurity web) {
    // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
    web
        .ignoring()
        .antMatchers("/h2-console/**");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.cors();
    http.csrf().disable().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.headers().frameOptions().disable();
    http.authorizeRequests();
    // 회원 관리 처리 API (POST /user/**) 에 대해 CSRF 무시

    http.authorizeRequests()
        // login 없이 허용
        .antMatchers("/user/login").permitAll()
        .antMatchers("/signup").permitAll()
        .antMatchers("/oauth/callback/kakao").permitAll()
        .antMatchers("/signup/checkid").permitAll()
        .antMatchers("/signup/checknick").permitAll()
        .antMatchers("/posts").permitAll()
        .antMatchers("/posts/**").permitAll()
        .antMatchers("/ws-stomp").permitAll()
        .antMatchers("/**").permitAll()

        //추가 - 메인 페이지 접근 허용
        .antMatchers("/").permitAll()

        // 그 외 어떤 요청이든 '인증'과정 필요
        .anyRequest().authenticated()
        .and()
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class);
  }
}