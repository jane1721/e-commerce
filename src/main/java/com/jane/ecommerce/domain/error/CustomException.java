package com.jane.ecommerce.domain.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private String[] msgArgs;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String[] msgArgs) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.msgArgs = msgArgs;
    }

    public CustomException(ErrorCode errorCode, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCode errorCode, String[] msgArgs, Throwable throwable) {
        super(errorCode.getMessage(), throwable);
        this.errorCode = errorCode;
        this.msgArgs = msgArgs;
    }

    /**
     * 동적으로 메시지를 형식화하여 반환합니다.
     * BaseErrorCode 에 정의된 메시지의 "{}"를 msgArgs 로 대체합니다.
     */
    public String formatMessage() {
        String message = errorCode.getMessage();
        if (msgArgs != null && msgArgs.length > 0) {
            for (String arg : msgArgs) {
                message = message.replaceFirst("\\{\\}", arg); // "{}"를 msgArgs 값으로 대체
            }
        }
        return message;
    }
}
