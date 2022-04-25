package com.hanghae99.finalprooject.jwt;

import com.hanghae99.finalprooject.exception.ErrorCode;
import com.hanghae99.finalprooject.exception.PrivateException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static java.lang.Long.parseLong;


public class SecurityUtil {

    public static Long getCurrentUserId() {
        // 인증 정보 꺼내기
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new PrivateException(ErrorCode.NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT);
        }
        return parseLong(authentication.getName());
    }
}