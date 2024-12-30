package com.green.greengram.feed.like.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
/*
    immutable(불변성)하게 객체를 만들고 싶다. 그러면 Setter 빼야함.
    private한 멤버필드에 값 넣는 방법 2가지. (생성자와 Setter)
    Setter을 빼기로 했기 때문에 남은 선택지는 생성자만 남았다.
    생성자를 이용해서 객체 생성을 해야 하는데 멤버필드값을 세팅하는 경우의 수가 많을 수 있다.

    1. feedId만 세팅한다.
    2. userId만 세팅한다.
    3. createdAt만 세팅한다.
    4. feedId, userId만 세팅한다.
    5. feedId, createAt 만 세팅한다.
    6. userId, createdAt만 세팅한다.
    7. userId, createdAt, feedId 다 세팅한다.
    8. 아무것도 세팅 안 한다.

    너무 많은 경우의 수를 줄여주는 게 builder패턴. 원하는 값을 탁탁 잘 넣어준다~

 */

@Getter
@EqualsAndHashCode // Equals 동등하냐고 묻는 것, 동일성이 아님. 이것의 최상위 부모는 Object. Object를 상속받는다.오버라이딩 중.
// 오버라이딩은 부모가 가지고 있는 선언부 똑같이 적어야 함. 구현부는 달라야 함..
// 오버로딩이랑 헷갈리지 말자. 이름은 같지만 파라미터가 다른 것... 이 애노테이션은 자동 오버라이딩 해 줌.
// ==은 동일성 비교, equals는 동등성 비교
@Builder

public class FeedLikeVo {

    private long feedId;
    private long userId;
    private String createdAt;

}
