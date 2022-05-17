package com.hanghae99.finalproject.validator;

import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;

import java.util.regex.Pattern;

public class MailValidator {

    public static void validateEmail(String email) {

        String patternEmail = "^[a-zA-Z0-9+-_.]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$";

        if (!Pattern.matches(patternEmail, email)) {
            throw new CustomException(ErrorCode.EMAIL_WRONG_PATTERN);
        }
    }
}