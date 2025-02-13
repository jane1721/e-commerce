package com.jane.ecommerce.domain.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // General Error
    SUCCESS(HttpStatus.OK, "Success"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "Invalid parameter"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "Method not allowed"),
    CONFLICT(HttpStatus.CONFLICT, "Conflict"),
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type"),

    // Custom Error
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "Insufficient stock for item"),
    INSUFFICIENT_BALANCE(HttpStatus.CONFLICT, "Insufficient fund for user"),
    LOCK_FAILED(HttpStatus.CONFLICT, "Lock failed"),
    INSUFFICIENT_COUPON_STOCK(HttpStatus.CONFLICT, "Insufficient stock for coupon"),
    DUPLICATE_COUPON_CLAIM(HttpStatus.CONFLICT, "Duplicate coupon claim");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
