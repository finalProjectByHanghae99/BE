package com.hanghae99.finalproject.sse.model;


import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

@Getter
@Embeddable
@NoArgsConstructor
public class RelatedURL {
    private static final int MAX_LENGTH = 255;

    @Column(nullable = false,length = MAX_LENGTH)
    private String url;

    public RelatedURL(String url) {
        if (isNotValidRelatedURL(url)) {
            throw new CustomException(ErrorCode.NOT_VALIDURL);
        }
        this.url = url;
    }

    private boolean isNotValidRelatedURL(String url) {
        return Objects.isNull(url) || url.length() > MAX_LENGTH || url.isEmpty();

    }

}
