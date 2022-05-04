package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.user.dto.LoginDto;
import com.hanghae99.finalproject.user.dto.SignOutDto;
import com.hanghae99.finalproject.user.dto.SignupDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.user.model.RefreshToken;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.repository.RefreshTokenRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.security.jwt.JwtReturn;
import com.hanghae99.finalproject.security.jwt.JwtTokenProvider;
import com.hanghae99.finalproject.security.jwt.TokenDto;
import com.hanghae99.finalproject.security.jwt.TokenRequestDto;
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
    private final PostRepository postRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        }//

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
                        .major(requestDto.getMajor())
                        .intro("자기 소개를 입력해주세요")
                        .profileImg("https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/basicProfile.png")
                        .portfolioLink("작성한 포트폴리오 URL이 없습니다")
                        .build()
        );
    }

    // 로그인
    @Transactional
    public TokenDto login(LoginDto loginDto) {
        UserValidator.validateEmailEmpty(loginDto);
        UserValidator.validatePasswordEmpty(loginDto);

        User user = userRepository.findByEmail(loginDto.getEmail()).orElseThrow(
                () -> new PrivateException(ErrorCode.LOGIN_NOT_FOUNT_EMAIL)
        );

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new PrivateException(ErrorCode.LOGIN_PASSWORD_NOT_MATCH);
        }

        String sub = String.valueOf(user.getId());
        String email = loginDto.getEmail();
        String nickname = user.getNickname();
        String major = user.getMajor();
        String profileImgUrl = user.getProfileImg();

        TokenDto tokenDto = jwtTokenProvider.createToken(sub, email, nickname, major, profileImgUrl);

        RefreshToken refreshToken = new RefreshToken(loginDto.getEmail(),tokenDto.getRefreshToken());
        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    // Token 재발급
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        log.info("Refresh Token : " + tokenRequestDto.getRefreshToken());

        // RefreshToken 만료됐을 경우
        if (jwtTokenProvider.validateToken(tokenRequestDto.getRefreshToken()) != JwtReturn.SUCCESS) {
            throw new PrivateException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = userRepository.findById(tokenRequestDto.getUserId()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        String sub = "mo-hum";
        String email = user.getEmail();
        String nickname = user.getNickname();
        String major = user.getMajor();
        String profile = user.getProfileImg();

        // RefreshToken DB에 없을 경우
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(email).orElseThrow(
                () -> new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );

        // RefreshToken 일치하지 않는 경우
        if (!refreshToken.getRefreshValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_MATCH);
        }

        // Access Token, Refresh Token 재발급
        TokenDto tokenDto = jwtTokenProvider.createToken(sub, email, nickname, major, profile);
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
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        log.info("DB 저장된 username : " + user.getNickname());

        if (!(user.getNickname().equals(loginUser))) {
            throw new PrivateException(ErrorCode.NOT_MATCH_USER_INFO);
        }
        userRepository.deleteById(user.getId());
    }

    // 로그아웃
    @Transactional
    public void deleteRefreshToken(TokenRequestDto tokenRequestDto) {
        User user = userRepository.findById(tokenRequestDto.getUserId()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        String email = user.getEmail();

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshKey(email).orElseThrow(
                () -> new PrivateException(ErrorCode.REFRESH_TOKEN_NOT_FOUND)
        );
        refreshTokenRepository.deleteById(refreshToken.getRefreshKey());
    }


}