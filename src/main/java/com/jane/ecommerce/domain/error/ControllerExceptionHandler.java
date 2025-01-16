package com.jane.ecommerce.domain.error;

import com.jane.ecommerce.interfaces.dto.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        log.error("handleException ==> {}", e.getMessage(), e);

        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

        BaseResponse baseResponse = new BaseResponse(errorCode.getMessage(), errorCode.getHttpStatus().toString());

        return new ResponseEntity<>(baseResponse, errorCode.getHttpStatus());
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse> handleCustomException(CustomException e) {

        String message;
        if (e.getPayload() == null) {
            log.error("handleCustomException ==> {}", e.getMessage(), e);
            message = e.getMessage();
        } else {
            log.error("handleCustomException ==> {} {}", e.getMessage(), e.getPayload(), e);
            message = e.getMessage() + " " + e.getPayload().toString();
        }

        ErrorCode errorCode = e.getErrorCode();

        BaseResponse baseResponse = new BaseResponse(message, errorCode.getHttpStatus().toString());

        return new ResponseEntity<>(baseResponse, errorCode.getHttpStatus());
    }
}
