package com.green.greengram.feed.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.beans.ConstructorProperties;

@Getter
@ToString
@EqualsAndHashCode
public class FeedDelReq {
        @Schema(name = "feed_id")
        private Long feedId;
        @JsonIgnore
        private long signedUserId;

        @ConstructorProperties({"feed_id"})
        public FeedDelReq(Long feedId) {
            this.feedId = feedId;

        }

        public void setSignedUserId(long signedUserId) {
                this.signedUserId = signedUserId;
        }
}
