package com.jane.ecommerce.base.exception;


import com.jane.ecommerce.base.dto.BaseErrorCode;
import com.jane.ecommerce.base.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class BaseControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {

        BaseErrorCode baseErrorCode = BaseErrorCode.INTERNAL_SERVER_ERROR;

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(baseErrorCode.getMessage());
        baseResponse.setStatus(baseErrorCode.getHttpStatus().toString());

        return ResponseEntity
                .status(BaseErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus())
                .body(baseResponse);
    }

    @ExceptionHandler(BaseCustomException.class)
    public ResponseEntity<BaseResponse> handleBaseCustomException(BaseCustomException e) {

        BaseErrorCode baseErrorCode = e.getBaseErrorCode();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(baseErrorCode.getMessage());
        baseResponse.setStatus(baseErrorCode.getHttpStatus().toString());

        return ResponseEntity
            .status(e.getBaseErrorCode().getHttpStatus())
            .body(baseResponse);
    }
}
