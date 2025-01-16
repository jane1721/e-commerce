package com.jane.ecommerce.domain.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private Object payload;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Object payload) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.payload = payload;
    }

    public CustomException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, Object payload, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
        this.payload = payload;
    }
}
