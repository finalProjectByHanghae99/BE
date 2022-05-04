package com.hanghae99.finalproject.timeConversion;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class MessageTimeConversion {
    public static String timeConversion(LocalDateTime modifiedAt){
        //현재시간
        LocalDateTime currentTime = LocalDateTime.now();
        //현재 시간과 수정 시간의 차이를 구함.
        Long timeDiff = Duration.between(modifiedAt, currentTime).getSeconds();
        String resultConversion = "";

        if((timeDiff / 60 / 60 / 24 / 7) > 0){//주
            resultConversion = modifiedAt.getMonth()+"월 "+modifiedAt.getDayOfMonth()+"일";
        }
        else if ((timeDiff / 60 / 60 / 24) > 0) { // 일
            resultConversion = timeDiff / 60 / 60 / 24 + "일 전";
        } else{
            if(modifiedAt.get(ChronoField.AMPM_OF_DAY) == 0) {
                resultConversion = "오전 "+modifiedAt.getHour()+":"+String.format("%02d", modifiedAt.getMinute());
            } else {
                resultConversion = "오후 "+modifiedAt.getHour()+":"+String.format("%02d", modifiedAt.getMinute());
            }
        }
        return resultConversion;

    }
}
