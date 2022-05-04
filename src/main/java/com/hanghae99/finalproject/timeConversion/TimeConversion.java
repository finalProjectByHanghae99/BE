package com.hanghae99.finalproject.timeConversion;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeConversion {
    public static String timeConversion(LocalDateTime modifiedAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        Long timeDiff = Duration.between(modifiedAt, currentTime).getSeconds();
        String resultConversion = "";

        if ((timeDiff / 60 / 60 / 24) > 0) { // 일
            resultConversion = timeDiff / 60 / 60 / 24 + "일 전";
        } else if ((timeDiff / 60 / 60) > 0) { // 시간
            resultConversion = timeDiff / 60 / 60 + "시간 전";
        } else if ((timeDiff / 60) > 0) { // 분
            resultConversion = timeDiff / 60 + "분 전";
        } else {
            resultConversion = timeDiff + "초 전";
        }

        return resultConversion;
    }
}
