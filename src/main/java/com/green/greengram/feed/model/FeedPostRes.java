package com.green.greengram.feed.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(title = "피드 등록 응답")
@EqualsAndHashCode
public class FeedPostRes {
    @Schema(title = "피드 PK")
    private long feedId;
    @Schema(title = "피드 사진 리스트")
    private List<String> pics;
}
