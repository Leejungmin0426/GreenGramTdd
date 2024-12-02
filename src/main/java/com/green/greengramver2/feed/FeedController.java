package com.green.greengramver2.feed;

import com.green.greengramver2.common.ResultResponse;
import com.green.greengramver2.feed.model.FeedGetReq;
import com.green.greengramver2.feed.model.FeedGetRes;
import com.green.greengramver2.feed.model.FeedPostReq;
import com.green.greengramver2.feed.model.FeedPostRes;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService service;

    @PostMapping
    public ResultResponse<FeedPostRes> postFeed(
            @RequestPart List<MultipartFile> pics,
            @RequestPart FeedPostReq p) {
        log.info("FeedController > postFeed > pics size: {}", pics.size());
        FeedPostRes res = service.postFeed(pics, p);
        return ResultResponse.<FeedPostRes>builder()
                .resultMessage("피드 등록 완료")
                .resultData(res)
                .build();
    }

    @GetMapping
    @Operation(summary = "Feed 리스트", description = "loginUserId는 로그인한 사용자의 pk")
    public ResultResponse<List<FeedGetRes>> getFeedList(@ParameterObject @ModelAttribute FeedGetReq p) {
        log.info("FeedController > getFeedList > p : {}", p);
        List<FeedGetRes> list = service.getFeedList(p);


        return ResultResponse.<List<FeedGetRes>>builder()
                .resultMessage(String.format("%d rows", list.size()))
                .resultData(list)
                .build();
    }






//    @GetMapping
//    @Operation(summary = "사진 및 좋아요 상태 조회", description = "특정 피드의 사진 리스트와 좋아요 상태를 반환합니다.")
//    public ResultResponse<List<FeedGetRes>> selFeedList(@ParameterObject @ModelAttribute FeedGetReq p) {
//        log.info("FeedController > selFeedList > p : {}", p);
//
//
//        List<FeedGetRes> list = service.selFeedList(p);
//        return ResultResponse.<List<FeedGetRes>>builder()
//                .resultMessage(String.format("%d개의 항목이 조회되었습니다.", list.size()))
//                .resultData(list)
//                .build();
//    }


    @PostMapping("/{feedId}")
    public ResponseEntity<String> getFeedPics(
            @PathVariable Long feedId,
            HttpServletRequest request) {

        // 클라이언트 IP 추적
        String clientIp = getClientIp(request);

        // 로그 출력 (SLF4J 사용)
        log.info("Client IP: {}", clientIp);
        log.info("Requested Feed ID: {}", feedId);

        return ResponseEntity.ok("Feed data fetched successfully.");
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}






