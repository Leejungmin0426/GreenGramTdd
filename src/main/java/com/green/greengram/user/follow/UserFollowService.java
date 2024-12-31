package com.green.greengram.user.follow;

import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.user.follow.model.UserFollowReq;
import com.green.greengram.user.model.UserSignInRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowMapper mapper;
    private final AuthenticationFacade authenticationFacade;

    public int postUserFollow(UserFollowReq p) {
        p.setFromUserId(authenticationFacade.getSignedUserId());
        return mapper.postUserFollow(p);
    }

    public int deleteUserFollow(UserFollowReq p) {
        p.setFromUserId(authenticationFacade.getSignedUserId());
        return mapper.deleteUserFollow(p);
    }
}

