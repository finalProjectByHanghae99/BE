package com.hanghae99.finalprooject.exception;

import lombok.Getter;

@Getter
public class PrivateException extends RuntimeException {

    private final ErrorCode errorCode;

    public PrivateException(ErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}