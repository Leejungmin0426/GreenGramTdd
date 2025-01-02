package com.green.greengram.feed.model;

import com.green.greengram.feed.comment.model.FeedCommentDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode
public class FeedWithPicCommentDto {
    private long feedId;
    private String contents;
    private String location;
    private String createdAt;
    private long writerUserId;
    private String writerNm;
    private String writerPic;
    private int isLike;

    private List<String> pics;
    private List<FeedCommentDto> commentList;
}
