package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.security.jwt.JwtReturn;
import com.hanghae99.finalproject.security.jwt.JwtTokenProvider;
import com.hanghae99.finalproject.security.jwt.TokenDto;
import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
import com.hanghae99.finalproject.user.dto.KakaoUserInfo;
import com.hanghae99.finalproject.user.dto.LoginDto;
import com.hanghae99.finalproject.user.dto.SignOutDto;
import com.hanghae99.finalproject.user.dto.SignupDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void registerUser(SignupDto.RequestDto requestDto) {

        // 회원 이메일 중복 확인
        String email = requestDto.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.SIGNUP_EMAIL_DUPLICATE_CHECK);
        }

        // 회원 닉네임 중복 확인
        String nickname = requestDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.SIGNUP_NICKNAME_DUPLICATE_CHECK);
        }

        // 회원 비밀번호 암호화
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 유효성 검사
        UserValidator.validateInputEmail(requestDto);
        UserValidator.validateInputPassword(requestDto);
        UserValidator.validateInputNickname(requestDto);
        UserValidator.validateInputMajor(requestDto);

        User user = userRepository.save(
                User.builder()
                        .email(requestDto.getEmail())
                        .nickname(requestDto.getNickname())
                        .password(password)
                        .major(requestDto.getMajor())
                        .profileImg("https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/profile_default.png")
                        .build()
        );
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        UserValidator.validateEmailEmpty(loginDto);
        UserValidator.validatePasswordEmpty(loginDto);

        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new CustomException(ErrorCode.LOGIN_NOT_FOUNT_EMAIL)
        );

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_PASSWORD_NOT_MATCH);
        }

//        String sub = String.valueOf(user.getId());
//        String email = loginDto.getEmail();
//        String nickname = user.getNickname();
//        String major = user.getMajor();
//        String profileImgUrl = user.getProfileImg();

        TokenDto tokenDto = jwtTokenProvider.createToken(user);

        RefreshToken refreshToken = new RefreshToken(loginDto.getEmail(),tokenDto.getRefreshToken());
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
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(user.getEmail()).orElseThrow(
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
        String email = user.getEmail();

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(email).orElseThrow(
                () -> new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );
        refreshTokenRepository.deleteById(refreshToken.getRefreshKey());
    }

    // 로그인 유저 상태 확인
    public StatusResponseDto SignupUserCheck(Long id) {

        User loginUser = userRepository.findById(id).orElse(null);

        KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
                .kakaoId(id)
                .isProfileSet(false)
                .build();

        if (loginUser == null) {
            return new StatusResponseDto("추가 정보 작성이 필요한 유저입니다", kakaoUserInfo);
        } else {
            TokenDto tokenDto = jwtTokenProvider.createToken(loginUser);
            return new StatusResponseDto("로그인 성공", tokenDto);
        }
    }

    @Transactional
    public TokenDto addInfo(SignupDto.RequestDto requestDto) {

        // 회원 닉네임 중복 확인
        String nickname = requestDto.getNickname();
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.SIGNUP_NICKNAME_DUPLICATE_CHECK);
        }

        // 유효성 검사
        UserValidator.validateInputNickname(requestDto);
        UserValidator.validateInputMajor(requestDto);

        User user = userRepository.save(
                User.builder()
                        .nickname(requestDto.getNickname())
                        .major(requestDto.getMajor())
                        .profileImg("https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/profile_default.png")
                        .build()
        );

        TokenDto tokenDto = jwtTokenProvider.createToken(user);

        RefreshToken refreshToken = new RefreshToken(requestDto.getNickname(), tokenDto.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }
}