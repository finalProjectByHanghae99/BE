package com.hanghae99.finalprooject.service;

import com.hanghae99.finalprooject.dto.userDto.*;
import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import com.hanghae99.finalprooject.model.RefreshToken;
import com.hanghae99.finalprooject.model.User;
import com.hanghae99.finalprooject.repository.RefreshTokenRepository;
import com.hanghae99.finalprooject.repository.UserRepository;
import com.hanghae99.finalprooject.security.jwt.JwtTokenProvider;
import com.hanghae99.finalprooject.security.jwt.TokenDto;
import com.hanghae99.finalprooject.security.jwt.TokenRequestDto;
import com.hanghae99.finalprooject.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(SignupDto.RequestDto requestDto) {

        // 회원 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new PrivateException(ErrorCode.DUPLICATE_CHECK_SIGNUP_EMAIL);
        }

        // 회원 닉네임 중복 확인
        String nickname = requestDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new PrivateException(ErrorCode.DUPLICATE_CHECK_SIGNUP_NICKNAME);
        }

        // 회원 비밀번호 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 유효성 검사
        UserValidator.validateInputEmail(requestDto);
        UserValidator.validateInputPassword(requestDto);
        UserValidator.validateInputNickname(requestDto);

        User user = userRepository.save(
                User.builder()
                        .email(requestDto.getEmail())
                        .nickname(requestDto.getNickname())
                        .password(password)
//                        .intro("자시소개를 해주세요")
//                        .profileImg("dfdfdfdfdff.png")
                        .build()
        );
    }

//    @Transactional
//    public TokenDto login(LoginDto loginDto) {
//        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();
//
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//
////        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
//
//        TokenDto tokenDto = jwtTokenProvider.createToken(loginDto.getEmail(), ,
//                loginDto.getEmail());
//
//        RefreshToken refreshToken = RefreshToken.builder()
//                .refreshKey(authentication.getName())
//                .refreshValue(tokenDto.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);
//        return tokenDto;
//    }



//    // 로그인
    @Transactional
    public TokenDto login(LoginDto loginDto) {

        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new IllegalArgumentException("해당 이메일이 없습니다")
        );

//        User user = userRepository.findByUsername(requestDto.getUsername()).orElseThrow(
//                () -> new DockingException(ErrorCode.USERNAME_NOT_FOUND)
//        );
//        validateLogin(requestDto, user);


        return jwtTokenProvider.createToken(loginDto.getEmail(), loginDto.getEmail());
    }


//    // Token 재발급
//    @Transactional
//    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
//        log.info("Access Token : " + tokenRequestDto.getAccessToken());
//        log.info("Refresh Token : " + tokenRequestDto.getRefreshToken());
//
//        // RefreshToken 만료됐을 경우
//        if (!jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
//            throw new PrivateException(ErrorCode.REFRESH_TOKEN_EXPIRED);
//        }
//
//        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//
//        // RefreshToken DB에 없을 경우
//        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(authentication.getName()).orElseThrow(
//                () -> new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
//        );
//
//        // RefreshToken 일치하지 않는 경우
//        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
//            throw new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
//        }
//        log.info("RefreshToken 만료 및 일치 확인");
//
//        // Access Token, Refresh Token 재발급
//        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);
//        RefreshToken updateRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
//        refreshTokenRepository.save(updateRefreshToken);
//
//        return tokenDto;
//    }

    // 회원 탈퇴
//    @Transactional
//    public void deleteUser(SignOutDto signOutDto) {
//        String loginUser = signOutDto.getNickname();
//        log.info("로그인 username : " + loginUser);
//
//        User user = userRepository.findByNickname(SecurityUtil.getCurrentUserNickname()).orElseThrow(
//                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
//        );
//        log.info("DB 저장된 username : " + user.getNickname());
//
//        if (!(user.getNickname().equals(loginUser))) {
//            throw new PrivateException(ErrorCode.NOT_MATCH_USER_INFO);
//        }
//        userRepository.deleteById(user.getId());
//    }

//    // 로그아웃
//    @Transactional
//    public void deleteRefreshToken(TokenRequestDto tokenRequestDto) {
//        Authentication authentication = jwtTokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
//        RefreshToken token = refreshTokenRepository.findByRefreshKey(authentication.getName()).orElseThrow(
//                () -> new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
//        );
//        refreshTokenRepository.deleteById(token.getRefreshKey());
//    }
}