package com.jane.ecommerce.interfaces.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jane.ecommerce.domain.error.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message = ErrorCode.SUCCESS.getMessage(); // 예외 메세지
    private String status = ErrorCode.SUCCESS.getHttpStatus().toString(); // HTTP 상태 코드
}
