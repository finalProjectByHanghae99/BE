package com.hanghae99.finalproject.timeConversion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeConversion {
    public static String timeConversion(LocalDateTime createdAt) {
        LocalDateTime currentTime = LocalDateTime.now();
        long timeDiff = Duration.between(createdAt, currentTime).getSeconds();
        String resultConversion = "";

        if ((timeDiff / 60 / 60 / 24) > 0) { // 일
            resultConversion = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(createdAt);
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