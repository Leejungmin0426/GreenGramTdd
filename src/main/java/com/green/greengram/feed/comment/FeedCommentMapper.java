package com.green.greengram.feed.comment;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

import com.green.greengram.feed.comment.model.FeedCommentDelReq;
import com.green.greengram.feed.comment.model.FeedCommentDto;
import com.green.greengram.feed.comment.model.FeedCommentGetReq;
import com.green.greengram.feed.comment.model.FeedCommentPostReq;

@Mapper
public interface FeedCommentMapper {
    void insFeedComment(FeedCommentPostReq p);
    List<FeedCommentDto> selFeedCommentList(FeedCommentGetReq p);
    List<FeedCommentDto> selFeedCommentListByFeedIdsLimit4(List<Long> feedIds);
    List<FeedCommentDto> selFeedCommentListByFeedIdsLimit4Ver2(List<Long> feedIds);
    int delFeedComment(FeedCommentDelReq p);
}