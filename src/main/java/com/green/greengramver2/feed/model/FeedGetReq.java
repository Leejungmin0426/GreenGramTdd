package com.green.greengramver2.feed.model;

import com.green.greengramver2.common.model.Paging;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.BindParam;

@Slf4j
@Getter
@ToString(callSuper = true)
public class FeedGetReq extends Paging {
    @Schema(title = "로그인 유저 PK", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private long signedUserId;

  public FeedGetReq (Integer size, Integer page,@BindParam("signed_user_id") long signedUserId){
      super(size, page);
      log.info("page: {}, size: {}, userId: {}", page, size, signedUserId);
      this.signedUserId = signedUserId;
  }
}
