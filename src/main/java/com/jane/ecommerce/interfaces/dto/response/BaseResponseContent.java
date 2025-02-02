package com.jane.ecommerce.interfaces.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BaseResponseContent extends BaseResponse {

    private Object content;

    public BaseResponseContent(Object content) {
        this.content = content;
    }
}
