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
}
