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
public class NotificationContent {

    private static final int Max_LENGTH = 50;

    @Column(nullable = false,length = Max_LENGTH)
    private String content;

    public NotificationContent(String content){
        if(isNotValidNotificationContent(content)){
            throw new CustomException(ErrorCode.NOT_VALIDCONTENT);
        }
        this.content = content;
    }

    private boolean isNotValidNotificationContent(String content) {
        return Objects.isNull(content) || content.length() > Max_LENGTH
        || content.isEmpty();

    }
}
