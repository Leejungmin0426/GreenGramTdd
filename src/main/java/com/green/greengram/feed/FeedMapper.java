package com.green.greengram.feed;

import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Mapper
@Validated
public interface FeedMapper {
    int insFeed (FeedPostReq p);



    List<FeedGetRes> selFeedList (FeedGetReq p);
    List<FeedAndPicDto> selFeedWithPicList(FeedGetReq p);
    List<FeedWithPicCommentDto> selFeedWithPicAndCommentLimit4List(FeedGetReq p);

    int deleteFeed (FeedDelReq p);



//    int checkLikeExists(long feedId, long userId);
//    void insertLike(long feedId, long userId);
}
