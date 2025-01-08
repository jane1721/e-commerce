package com.jane.ecommerce.base.exception;

import com.jane.ecommerce.base.dto.BaseErrorCode;
import lombok.Getter;

@Getter
public class BaseCustomException extends RuntimeException {

    private final BaseErrorCode baseErrorCode;
    private String[] msgArgs;

    public BaseCustomException(BaseErrorCode baseErrorCode) {
        super(baseErrorCode.getMessage());
        this.baseErrorCode = baseErrorCode;
    }

    public BaseCustomException(BaseErrorCode baseErrorCode, String[] msgArgs) {
        super(baseErrorCode.getMessage());
        this.baseErrorCode = baseErrorCode;
        this.msgArgs = msgArgs;
    }

    public BaseCustomException(BaseErrorCode baseErrorCode, Throwable throwable) {
        super(baseErrorCode.getMessage(), throwable);
        this.baseErrorCode = baseErrorCode;
    }

    public BaseCustomException(BaseErrorCode baseErrorCode, String[] msgArgs, Throwable throwable) {
        super(baseErrorCode.getMessage(), throwable);
        this.baseErrorCode = baseErrorCode;
        this.msgArgs = msgArgs;
    }

    /**
     * 동적으로 메시지를 형식화하여 반환합니다.
     * BaseErrorCode 에 정의된 메시지의 "{}"를 msgArgs 로 대체합니다.
     */
    public String formatMessage() {
        String message = baseErrorCode.getMessage();
        if (msgArgs != null && msgArgs.length > 0) {
            for (String arg : msgArgs) {
                message = message.replaceFirst("\\{\\}", arg); // "{}"를 msgArgs 값으로 대체
            }
        }
        return message;
    }
}
