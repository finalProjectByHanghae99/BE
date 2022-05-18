package com.hanghae99.finalproject.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalproject.user.dto.KakaoUserInfo;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${client_id}")
    private String client_id;

    @Value("${redirect_uri}")
    private String redirect_uri;

    public KakaoUserInfo kakaoLogin(String code) throws JsonProcessingException {
        // "인가 코드"로 AccessToken 요청
        String accessToken = getAccessToken(code);

        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(accessToken);

        User kakaoUser = userRepository.findByMemberId(kakaoUserInfo.getKakaoMemberId()).orElse(null);
        if (kakaoUser == null) {
            registerKakaoUser(kakaoUserInfo);
        }
        return getKakaoUserInfo(accessToken);
    }

    private String getAccessToken(String code) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> AccessToken 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();    // Json 형식 java에서 사용하기 위해 objectMapper 사용
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }

    // AccessToken 으로 카카오 사용자 정보 가져오기
    private KakaoUserInfo getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long kakaoId = jsonNode.get("id").asLong();
        String email = jsonNode.get("kakao_account").get("email").asText();
        return KakaoUserInfo.builder()
                .kakaoId(kakaoId)
                .kakaoMemberId(email)
                .build();
    }

    // 첫 소셜로그인일 경우 DB 저장
    private void registerKakaoUser(KakaoUserInfo kakaoUserInfo) {

        String password = UUID.randomUUID().toString();
        String encodedPassword = passwordEncoder.encode(password);

        User kakaoUser = User.builder()
                .kakaoId(kakaoUserInfo.getKakaoId())
                .memberId(kakaoUserInfo.getKakaoMemberId())
                .password(encodedPassword)
                .nickname("default")
                .major("default")
                .build();
        userRepository.save(kakaoUser);
    }
//
//    // 강제 로그인 처리
//    private LoginDto.KakaoLogin jwtTokenCreate(User kakaoUser) {
//
//        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        return
//    }
}
