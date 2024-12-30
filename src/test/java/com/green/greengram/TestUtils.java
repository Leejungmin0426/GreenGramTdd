package com.green.greengram;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUtils {
    //파라미터 dateTime으로 넘어오는 값이 DB에 저장된 dateTime 값 2024-12-30 11:32:23
    public static void assertCurrentTimeStamp(String dateTime){
        //자바에서 현재일시 데이터
        LocalDateTime expectedNow = LocalDateTime.now(); // 2024-12-30T11:32:23
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // M대문자는 달, m소문자는 분... 대소문자 중요함. HH는 24시간 기준, hh는 12시간 기준, YYYY는 WeekYser, yyyy는 숫자로 나옴
        LocalDateTime actualNow = LocalDateTime.parse(dateTime, formatter);
        assertTrue(Duration.between(expectedNow, actualNow).getSeconds() <= 1);
    }



}