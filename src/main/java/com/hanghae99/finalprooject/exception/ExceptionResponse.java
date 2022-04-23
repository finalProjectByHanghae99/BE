package com.hanghae99.finalprooject.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class ExceptionResponse {

    private HttpStatus status;
    private String errorCode;
    private String errorMessage;

    public ExceptionResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMessage();
    }
}