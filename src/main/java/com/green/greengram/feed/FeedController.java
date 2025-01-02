package com.green.greengram.feed;

import com.green.greengram.common.ResultResponse;
import com.green.greengram.feed.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("feed")
@Tag(name = "2. 피드", description = "피드 관리")
public class FeedController {
    private final FeedService service;

    @PostMapping
    public ResultResponse<FeedPostRes> postFeed(
            @RequestPart List<MultipartFile> pics,
            @RequestPart FeedPostReq p) {
        log.info("FeedController > postFeed > pics size: {}", pics.size());
        FeedPostRes res = service.postFeed(pics, p);
        return ResultResponse.<FeedPostRes>builder()
                .resultMessage("피드 등록 완료입니다아앙~♥")
                .resultData(res)
                .build();
    }

////    @GetMapping
//    @Operation(summary = "Feed 리스트", description = "loginUserId는 로그인한 사용자의 pk")
//    public ResultResponse<List<FeedGetRes>> getFeedList(@ParameterObject @ModelAttribute FeedGetReq p) {
//        log.info("FeedController > getFeedList > p : {}", p);
//        List<FeedGetRes> list = service.getFeedList(p);
//
//
//        return ResultResponse.<List<FeedGetRes>>builder()
//                .resultMessage(String.format("%d rows", list.size()))
//                .resultData(list)
//                .build();
//    }




    @GetMapping
    @Operation(summary = "Feed 리스트", description = "loginUserId는 로그인한 사용자의 pk")
    public ResultResponse<List<FeedGetRes>> getFeedList(@Valid @ParameterObject @ModelAttribute FeedGetReq p) {
        log.info("FeedController > getFeedList > p : {}", p);
        List<FeedGetRes> list = service.getFeedList(p);


        return ResultResponse.<List<FeedGetRes>>builder()
                .resultMessage(String.format("%d rows", list.size()))
                .resultData(list)
                .build();
    }


    @GetMapping("ver4")
    @Operation(summary = "Feed 리스트 - No N+1 - using Mybatis", description = "signed_user_id는 로그인한 사용자의 pk")
    public ResultResponse<List<FeedGetRes>> getFeedListVer6(@Valid @ParameterObject @ModelAttribute FeedGetReq p) {
        log.info("FeedController > getFeedListVer4 > p: {}", p);
        List<FeedGetRes> list = service.getFeedList6(p);
        return ResultResponse.<List<FeedGetRes>>builder()
                .resultMessage(String.format("%d rows", list.size()))
                .resultData(list)
                .build();
    }




//    @GetMapping
//    @Operation(summary = "사진 및 좋아요 상태 조회", description = "특정 피드의 사진 리스트와 좋아요 상태를 반환합니다.")
//    public ResultResponse<List<FeedGetRes>> getFeedList2(@ParameterObject @ModelAttribute FeedGetReq p) {
//        log.info("FeedController > selFeedList > p : {}", p);
//
//        // FeedGetReq에서 feedIds와 currentUserId 추출
//        List<Long> feedIds = p.getFeedIds(); // feedIds를 추출
//        Long currentUserId = p.getSignedUserId(); // 현재 사용자 ID 추출
//
//        // 서비스 호출
//        List<FeedGetRes> list = service.getFeedList2(p);
//
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

    @DeleteMapping
    public ResultResponse<Integer> deleteFeed(@ParameterObject @ModelAttribute FeedDelReq p) {
        log.info("FeedController > deleteFeed > p: {}", p);
        int res = service.deleteFeed(p);
        return ResultResponse.<Integer>builder()
                .resultMessage("게시물 삭제가 완료되었습니다.")
                .resultData(res)
                .build();
    }

}






