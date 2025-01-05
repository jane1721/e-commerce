package com.jane.ecommerce.base.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BaseResponseContent extends BaseResponse {

    private Object content;

    public BaseResponseContent(Object content) {
        this.content = content;
    }
}
