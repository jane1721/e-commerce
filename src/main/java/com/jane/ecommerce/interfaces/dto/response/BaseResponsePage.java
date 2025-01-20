package com.jane.ecommerce.interfaces.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BaseResponsePage<T> extends BaseResponseContent {

    private int page;
    private int size;
    private long total;

    public BaseResponsePage(Page<T> page) {
        setContent(page.getContent());
        this.page = page.getNumber() + 1;
        this.size = page.getSize();
        this.total = page.getTotalElements();
    }
}
