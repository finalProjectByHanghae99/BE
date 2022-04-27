package com.hanghae99.finalprooject.jwt;

import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {

    public static String getCurrentUserNickname() {
        // 인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new PrivateException(ErrorCode.NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT);
        }

        String nickname = null;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
            nickname = springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof String) {
            nickname = (String) authentication.getPrincipal();
        }
        return nickname;
    }
}