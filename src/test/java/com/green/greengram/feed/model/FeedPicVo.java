package com.green.greengram.feed.model;

import lombok.Getter;

@Getter
//Setter 메소드가 없어서 Reflection API를 이용하여 데이터가 대입이 될 건데 이때 중요한 게 객체 생성이 무조건 되어야 함. 그래서 기본 생성자가 있는지 확인한다.
public class FeedPicVo {
    private long feedId;
    private String pic;
    private String createdAt;
}
