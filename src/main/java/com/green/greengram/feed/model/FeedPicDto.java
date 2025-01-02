package com.green.greengram.feed.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class FeedPicDto {

    private long feedId;
    private List<String> pics;

}
