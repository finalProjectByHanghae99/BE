package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.security.jwt.JwtReturn;
import com.hanghae99.finalproject.security.jwt.JwtTokenProvider;
import com.hanghae99.finalproject.security.jwt.TokenDto;
import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.*;
import com.hanghae99.finalproject.user.model.RefreshToken;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.RefreshTokenRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.color.ProfileDataException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 일반 회원가입
    @Transactional
    public UserInfo register(SignupRequestDto requestDto) {

        // 회원 아이디 중복 확인
        String memberId = requestDto.getMemberId();
        if (userRepository.existsByMemberId(memberId)) {
            throw new CustomException(ErrorCode.SIGNUP_MEMBERID_DUPLICATE_CHECK);
        }

        // 회원 비밀번호 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 유효성 검사
        UserValidator.validateInputMemberId(requestDto);
        UserValidator.validateInputPassword(requestDto);

        User user = userRepository.save(
                User.builder()
                        .memberId(requestDto.getMemberId())
                        .password(password)
                        .nickname("닉네임을 설정해주세요")
                        .major("전공을 선택해주세요")
                        .build()
        );

        return UserInfo.builder()
                .userId(user.getId())
                .isProfileSet(false)
                .build();
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        UserValidator.validateMemberIdEmpty(loginDto);
        UserValidator.validatePasswordEmpty(loginDto);

        User user = userRepository.findByMemberId(loginDto.getMemberId()).orElseThrow(
                () -> new CustomException(ErrorCode.LOGIN_NOT_FOUNT_MEMBERID)
        );

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_NOT_MATCH);
        }

        TokenDto tokenDto = jwtTokenProvider.createToken(user);

        RefreshToken refreshToken = new RefreshToken(loginDto.getMemberId(),tokenDto.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    // Token 재발급
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        log.info("Refresh Token : " + tokenRequestDto.getRefreshToken());

        UserValidator.validateRefreshTokenReissue(tokenRequestDto);

        // RefreshToken 만료됐을 경우
        if (jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken()) != JwtReturn.SUCCESS) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = userRepository.findById(tokenRequestDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // RefreshToken DB에 없을 경우
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(user.getMemberId()).orElseThrow(
                () -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );

        // RefreshToken 일치하지 않는 경우
        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        // Access Token, Refresh Token 재발급
        TokenDto tokenDto = jwtTokenProvider.createToken(user);
        RefreshToken updateRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return tokenDto;
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(SignOutDto signOutDto, UserDetailsImpl userDetails) {
        String loginUser = signOutDto.getNickname();
        log.info("로그인 username : " + loginUser);

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        log.info("DB 저장된 username : " + user.getNickname());

        if (!(user.getNickname().equals(loginUser))) {
            throw new CustomException(ErrorCode.NOT_MATCH_USER_INFO);
        }
        userRepository.deleteById(user.getId());
    }

    // 로그아웃
    @Transactional
    public void deleteRefreshToken(TokenRequestDto tokenRequestDto) {
        User user = userRepository.findById(tokenRequestDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        String memberId = user.getMemberId();

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(memberId).orElseThrow(
                () -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );
        refreshTokenRepository.deleteById(refreshToken.getRefreshKey());
    }

    // 로그인 유저 상태 확인
    public StatusResponseDto SignupUserCheck(Long id) {

        User loginUser = userRepository.findById(id).orElse(null);

        if (loginUser == null) {
            KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                    .id(id)
                    .isProfileSet(false)
                    .build();
            return new StatusResponseDto("추가 정보 작성이 필요한 유저입니다", kakaoUserInfo);
        } else {
            TokenDto tokenDto = jwtTokenProvider.createToken(loginUser);
            return new StatusResponseDto("로그인 성공", tokenDto);
        }
    }

    // 회원가입 추가 정보 등록
    @Transactional
    public TokenDto addInfo(SignupRequestDto requestDto) {

        // 닉네임 중복 확인
        String nickname = requestDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.SIGNUP_NICKNAME_DUPLICATE_CHECK);
        }

        // 유효성 검사
        UserValidator.validateInputNickname(requestDto);
        UserValidator.validateInputMajor(requestDto);

        // DB에서 유저 정보를 찾음
        User user = userRepository.findById(requestDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.SIGNUP_USERID_NOT_FOUND)
        );

        user.addInfo(requestDto);

//        if (user.getKakaoId() == null) {    // 일반회원가입 유저일 경우
//            user.addInfo(requestDto);
//        } else {    // 카카오 회원가입 유저일 경우
//            user.kakaoUserAddInfo(requestDto);
//        }

        TokenDto tokenDto = jwtTokenProvider.createToken(user);

        RefreshToken refreshToken = new RefreshToken(user.getMemberId(), tokenDto.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }
}