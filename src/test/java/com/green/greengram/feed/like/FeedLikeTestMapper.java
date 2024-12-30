package com.green.greengram.feed.like;

import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedLikeTestMapper {
    @Select("SELECT * FROM feed_like WHERE  feed_id = #{feedId} And user_id = #{userId}") // 튜플이 나올 수 있는 경우의 수 두가지. 넘어올 수 있거나 없거나. 이건 null이 넘어 올 수 있다.
    FeedLikeVo selFeedLikeByFeedIdAndUserId (FeedLikeReq p);

    @Select("SELECT * FROM feed_like")
    List<FeedLikeVo> selFeedLikeAll(); // 이러면 사이즈가 0인 Arraylist 객체가 넘어온다. null이 아님. list때는 절대 null은 안 넘어옴... 마이바티스라서~ SQL에선 다름!



}
