package com.green.greengram.feed.follow;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.user.follow.UserFollowMapper;
import com.green.greengram.user.follow.UserFollowService;
import com.green.greengram.user.follow.model.UserFollowReq;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
//Spring Test Context (컨테이너) 이용하는 거 아님
@ExtendWith(MockitoExtension.class)
public class UserFollowServiceTest {
        @InjectMocks // 시제품(Mock-목업할 때 목)을 inject할 거다.
    UserFollowService userFollowService; // Mockito가 객체화 해 줌

    @Mock
    UserFollowMapper userFollowMapper;

    @Mock // 가짜 객체를 만들어 주는 애노테이션. 열고 닫는 기능 정도만 가능
    AuthenticationFacade authenticationFacade;

    // 1 > 2 follow 한 내용은 있다.
    static final long fromUserId1 = 1L;
    static final long toUserId2 = 2L;

    static final long fromUserId3 = 3L;
    static final long toUserId4 = 4L;


    static final UserFollowReq userFollowReq1_2 = new UserFollowReq(toUserId2);
    static final UserFollowReq userFollowReq3_4 = new UserFollowReq(toUserId4);

    @Test
    @DisplayName("postUserFollow 테스트")
    void postUserFollow() {
        //given
        //authenticationFacade Mock객체의 getSignedUserId()메소드를 호출하면 willReturn값이 리턴이 되게끔 세팅
        final int EXPECTED_RESULT = 14; // 검증 시 뜬금없는 값 넣는 게 좋다. 검증이 더욱 정교해 짐.. 13같은
        final long FORM_USER_ID = fromUserId3;
        final long TO_USER_ID = toUserId4;
        given(authenticationFacade.getSignedUserId()).willReturn(FORM_USER_ID);

        UserFollowReq givenParam = new UserFollowReq(TO_USER_ID);
        givenParam.setFromUserId(FORM_USER_ID);
        given(userFollowMapper.deleteUserFollow(givenParam)).willReturn(EXPECTED_RESULT);

        //when
        UserFollowReq actualParam = new UserFollowReq(TO_USER_ID);
        int actualResult = userFollowService.deleteUserFollow(actualParam);

        //then
        assertEquals(EXPECTED_RESULT, actualResult);
    }
}