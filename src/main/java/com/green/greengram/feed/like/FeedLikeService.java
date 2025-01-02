package com.green.greengram.feed.like;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.like.model.FeedLikeReq;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@EqualsAndHashCode
public class FeedLikeService {
    private final FeedLikeMapper mapper;
    private final AuthenticationFacade authenticationFacade;
    public int feedLikeToggle(FeedLikeReq p) {
        p.setUserId(authenticationFacade.getSignedUserId());
        int result = mapper.delFeedLike(p);
        if (result == 0){
            return mapper.insFeedLike(p);// 좋아요 등록이 되었을 때 1 리턴
        }
        return 0; // 좋아요 취소가 되었을 때 0 리턴
    }

}
