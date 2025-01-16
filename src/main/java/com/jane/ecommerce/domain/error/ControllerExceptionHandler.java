package com.jane.ecommerce.domain.error;

import com.jane.ecommerce.interfaces.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(errorCode.getMessage());
        baseResponse.setStatus(errorCode.getHttpStatus().toString());

        return new ResponseEntity<>(baseResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse> handleCustomException(CustomException e) {

        ErrorCode errorCode = e.getErrorCode();

        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(e.formatMessage());
        baseResponse.setStatus(errorCode.getHttpStatus().toString());

        return new ResponseEntity<>(baseResponse, errorCode.getHttpStatus());
    }
}
