package com.green.greengram.feed.like.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "피드 좋아요 Toggle")
@EqualsAndHashCode
public class FeedLikeReq {
    @Schema(title = "피드 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long feedId;
    @Schema(title = "유저 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long userId;
}
