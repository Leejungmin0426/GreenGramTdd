package com.green.greengram.feed.like;


import com.green.greengram.feed.like.model.FeedLikeReq;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test") // yaml 적용되는 파일 선택 (application-test.yml)
@MybatisTest // Mybatis Mapper Test 이기 때문에 작성 >> Mapper들이 전부 객체화
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)

//테스트를 기본적으로 메모리 데이터베이스 (H2)를 사용해서 하는데 메모리 데이터베이스로 교체하지 않겠다.
        //wmr, 우리가 원래 쓰는 데이터베이스로 테스트를 진행하겠다.
class FeedLikeMapperTest {

    @Autowired //스프링 컨테이너가 DI해 주는 게 맞음
    private FeedLikeMapper feedLikeMapper; //필드 주입 방식의 DI가 된다.

    static final long FEED_ID_1 = 1L;
    static final long FEED_ID_5 = 5L;
    static final long USER_ID_2 = 2L;

    static final FeedLikeReq existedData = new FeedLikeReq();
    static final FeedLikeReq notExistedData = new FeedLikeReq();
    /*
        @BeforeAll - 모든 테스트 실행 전에 최초 한 번 실행
        @BeforeEach - 각 테스트 실행 전에 실행
        @Test -
        @AfterEach - 각 테스트 실행 후에 실행
        @AfterAll - 모든 테스트 실행 후에 최초 한 번 실행
    */
    // @BeforeEach - 테스트 메소드 마다 테스트 메소드 실행 전에 실행되는 메소드
    // before 메소드
    // @BeforeAll - 테스트 메소드 실행되기 최초 딱 한 번 실행이 되는 메소드
    // 테스트 메소드 마다 테스트 객체가 만들어지면 BeforeAll 메소드는 static 메소드여야 한다.
    // 한 테스트 객체가 만들어지면 non-static메소드여야 한다.


    @BeforeAll
    static void initData() {
        existedData.setFeedId(FEED_ID_1);
        existedData.setUserId(USER_ID_2);

        notExistedData.setFeedId(FEED_ID_5);
        notExistedData.setUserId(USER_ID_2);


    }

    @Test
    void insFeedLikeDuplicateDataThrowDuplicateKeyException() {

        assertThrows(DuplicateKeyException.class, () -> {
            feedLikeMapper.insFeedLike(existedData);
        }, "데이터 중복시 에러 발생되지 않음> primary key(feed_id, user_id) 확인 바람");
    }

    @Test
    void insFeedLikeNormal() {
        //given
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_5);
        givenParam.setUserId(USER_ID_2);

        //when
        int actualAffectedRows = 0;
        try {
            actualAffectedRows = feedLikeMapper.insFeedLike(notExistedData);
        } catch (Exception e) {
            //then: 예외 메시지 검증
            assertTrue(e.getMessage().contains("expected error message"), "예외 메시지가 예상과 다릅니다.");
            return; // 테스트 종료 (예외 검증이 완료되었으므로)
        }

        //then: 예외가 없었을 경우 정상 처리 확인
        assertEquals(1, actualAffectedRows, "insert 문제 발생");
    }

    @Test
    void delFeedLikeNoData() {
        FeedLikeReq givenParam = new FeedLikeReq();
        givenParam.setFeedId(FEED_ID_5);
        givenParam.setFeedId(USER_ID_2);

        int actualAffectedRows = feedLikeMapper.delFeedLike(notExistedData);

        assertEquals(0, actualAffectedRows);
    }


    @Test
    void delFeedLikeNormal() {
        int actualAffectedRows = feedLikeMapper.delFeedLike(existedData);
        assertEquals(1, actualAffectedRows);
    }
}