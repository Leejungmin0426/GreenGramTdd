package com.green.greengram.feed;

import com.green.greengram.common.model.MyFileUtils;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.comment.model.*;
import com.green.greengram.feed.like.FeedLikeMapper;
import com.green.greengram.feed.like.FeedLikeService;
import com.green.greengram.feed.like.model.FeedLikeReq;
import com.green.greengram.feed.like.model.FeedLikeRes;
import com.green.greengram.feed.model.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final FeedMapper feedMapper;
    private final FeedPicMapper feedPicMapper;
    private final FeedCommentMapper feedCommentMapper;
    private final FeedLikeMapper feedLikeMapper;
    private final MyFileUtils myFileUtils;
    private final FeedLikeService feedLikeService;
    private final AuthenticationFacade authenticationFacade;

    @Transactional
    //자동 커밋 종료
    public FeedPostRes postFeed(List<MultipartFile> pics, FeedPostReq p) {

        p.setWriterUserId(authenticationFacade.getSignedUserId());
        int result = feedMapper.insFeed(p);

        // --------------- 파일 등록
        long feedId = p.getFeedId();

        //저장 폴더 만들기, 저장위치/feed/${feedId}/파일들을 저장한다.
        String middlePath = String.format("feed/%d", feedId);
        myFileUtils.makeFolders(middlePath);

        //랜덤 파일명 저장용  >> feed_pics 테이블에 저장할 때 사용
        List<String> picNameList = new ArrayList<>(pics.size());
        for (MultipartFile pic : pics) {
            //각 파일 랜덤파일명 만들기
            String savedPicName = myFileUtils.makeRandomFileName(pic);
            picNameList.add(savedPicName);
            String filePath = String.format("%s/%s", middlePath, savedPicName);
            try {
                myFileUtils.transferTo(pic, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FeedPicDto feedPicDto = new FeedPicDto();
        feedPicDto.setFeedId(feedId);
        feedPicDto.setPics(picNameList);
        int resultPics = feedPicMapper.insFeedPic(feedPicDto);

        return FeedPostRes.builder()
                .feedId(feedId)
                .pics(picNameList)
                .build();
    }

//    public void likeFeed(long feedId, long userId) {
//        // 좋아요 중복 확인
//        int count = feedMapper.checkLikeExists(feedId, userId);
//        if (count == 0) {
//            feedMapper.insertLike(feedId, userId); // 좋아요 추가
//        } else {
//            throw new IllegalStateException("이미 좋아요를 누른 상태입니다.");
//        }
//    }

    public List<FeedGetRes> getFeedList(@Valid FeedGetReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        // N + 1 이슈 발생
        List<FeedGetRes> list = feedMapper.selFeedList(p);
        log.info("listTest = {}", list.toString());
//
        if (list.size() == 0) {
            return list;
        }

        for (int i = 0; i < list.size(); i++) {
            FeedGetRes item = list.get(i);
            //피드 당 사진 리스트
            item.setPics(feedPicMapper.selFeedPicList(item.getFeedId()));

//            for (FeedGetRes item : list) {
//                // 피드 당 사진 리스트 설정
//                item.setPics(feedPicsMapper.selFeedPics(item.getFeedId()));
//            }

            //피드 당 댓글 4개
            FeedCommentGetReq commentGetReq = new FeedCommentGetReq(item.getFeedId(), 0, 3);
            List<FeedCommentDto> commentList = feedCommentMapper.selFeedCommentList(commentGetReq); //0, 4

            FeedCommentGetRes commentGetRes = new FeedCommentGetRes();
            commentGetRes.setCommentList(commentList);
            commentGetRes.setMoreComment(commentList.size() == commentGetReq.getSize()); //4개면 true, 4개 아니면 false

            if (commentGetRes.isMoreComment()) {
                commentList.remove(commentList.size() - 1);
            }
            item.setComment(commentGetRes);
        }
        return list;
    }


    public List<FeedGetRes> getFeedList2(FeedGetReq p) {

        p.setSignedUserId(authenticationFacade.getSignedUserId());

        // 1. 피드 리스트 조회
        List<FeedGetRes> feedList = feedMapper.selFeedList(p);

        if (feedList.size() == 0) {
            return feedList;
        }

        // 2. 피드 ID 목록 추출
        List<Long> feedIds = feedList.stream()
                .map(FeedGetRes::getFeedId)
                .collect(Collectors.toList());
        if (feedIds.isEmpty()) return Collections.emptyList();

        // 3. 사진 및 댓글 데이터 조회
        Map<Long, List<String>> picHashMap = feedPicMapper.selFeedPicListByFeedIds(feedIds).stream()
                .collect(Collectors.groupingBy(
                        FeedPicSel::getFeedId,
                        Collectors.mapping(FeedPicSel::getPic, Collectors.toList())
                ));
        Map<Long, FeedCommentGetRes> commentHashMap = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds).stream()
                .collect(Collectors.groupingBy(
                        FeedCommentDto::getFeedId,
                        Collectors.collectingAndThen(Collectors.toList(), comments -> {
                            FeedCommentGetRes commentRes = new FeedCommentGetRes();
                            commentRes.setCommentList(comments.size() > 4
                                    ? comments.subList(0, 4)
                                    : comments);
                            commentRes.setMoreComment(comments.size() > 4);
                            return commentRes;
                        })
                ));

        // 4. 피드 리스트에 사진 및 댓글 매핑
        feedList.forEach(feed -> {
            feed.setPics(picHashMap.getOrDefault(feed.getFeedId(), Collections.emptyList()));
            feed.setComment(commentHashMap.getOrDefault(feed.getFeedId(), new FeedCommentGetRes()));
        });

        return feedList;
    }

    //select 3번, 피드 5,000개 있음, 페이지당 20개씩 가져온다.
    public List<FeedGetRes> getFeedList3(@Valid FeedGetReq p) {

        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 리스트
        List<FeedGetRes> list = feedMapper.selFeedList(p);

        //feed_id를 골라내야 한다.
        List<Long> feedIds4 = list.stream().map(FeedGetRes::getFeedId).collect(Collectors.toList());
        List<Long> feedIds5 = list.stream().map(item -> ((FeedGetRes) item).getFeedId()).toList();
        List<Long> feedIds6 = list.stream().map(item -> {
            return ((FeedGetRes) item).getFeedId();
        }).toList();

        List<Long> feedIds = new ArrayList<>(list.size());
        for (FeedGetRes item : list) {
            feedIds.add(item.getFeedId());
        }
        log.info("feedIds: {}", feedIds);

        //피드와 관련된 사진 리스트
        List<FeedPicSel> feedPicList = feedPicMapper.selFeedPicListByFeedIds(feedIds);
        log.info("feedPicList: {}", feedPicList);

        Map<Long, List<String>> picHashMap = new HashMap<>();
        for (FeedPicSel item : feedPicList) {
            long feedId = item.getFeedId();
            if (!picHashMap.containsKey(feedId)) {
                picHashMap.put(feedId, new ArrayList<>(3));
            }
            List<String> pics = picHashMap.get(feedId);
            pics.add(item.getPic());
        }

        //피드와 관련된 댓글 리스트
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>();
        for (FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if (!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>(4));
                commentHashMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId);
            feedCommentGetRes.getCommentList().add(item);
        }

        for (FeedGetRes res : list) {
            res.setPics(picHashMap.get(res.getFeedId()));
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if (feedCommentGetRes == null) {
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }
        log.info("list: {}", list);
        return list;
    }


    @Transactional
    public int deleteFeed(FeedDelReq p) {
        p.setSignedUserId(authenticationFacade.getSignedUserId());
        //피드 사진 삭제
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);
        //피드 삭제
        return feedMapper.deleteFeed(p);
    }

   /*

      @Transactional
    public int deleteFeed(FeedDeleteReq p) {
        //피드 사진 삭제
        String deletePath = String.format("%s/feed/%d", myFileUtils.getUploadPath(), p.getFeedId());
        myFileUtils.deleteFolder(deletePath, true);

        //피드 댓글, 좋아요 삭제
        int affectedRows = feedMapper.delFeedLikeAndFeedCommentAndFeedPic(p);
        log.info("affectedRows: {}", affectedRows);

        //피드 삭제
        return feedMapper.delFeed(p);
    }

    */


    public List<FeedGetRes> getFeedList4(FeedGetReq p) {
        // 1. 피드 리스트 조회
        List<FeedGetRes> feedList = feedMapper.selFeedList(p);

        // 2. 피드 ID 목록 추출
        List<Long> feedIds = feedList.stream()
                .map(FeedGetRes::getFeedId)
                .collect(Collectors.toList());
        if (feedIds.isEmpty()) return Collections.emptyList();

        // 3. 사진 및 댓글 데이터 조회
        Map<Long, List<String>> picHashMap = feedPicMapper.selFeedPicListByFeedIds(feedIds).stream()
                .collect(Collectors.groupingBy(
                        FeedPicSel::getFeedId,
                        Collectors.mapping(FeedPicSel::getPic, Collectors.toList())
                ));
        Map<Long, FeedCommentGetRes> commentHashMap = feedCommentMapper.selFeedCommentListByFeedIdsLimit4(feedIds).stream()
                .collect(Collectors.groupingBy(
                        FeedCommentDto::getFeedId,
                        Collectors.collectingAndThen(Collectors.toList(), comments -> {
                            FeedCommentGetRes commentRes = new FeedCommentGetRes();
                            commentRes.setCommentList(comments.size() > 4
                                    ? comments.subList(0, 4)
                                    : comments);
                            commentRes.setMoreComment(comments.size() > 4);
                            return commentRes;
                        })
                ));

        // 4. 피드 리스트에 사진 및 댓글 매핑
        feedList.forEach(feed -> {
            feed.setPics(picHashMap.getOrDefault(feed.getFeedId(), Collections.emptyList()));
            feed.setComment(commentHashMap.getOrDefault(feed.getFeedId(), new FeedCommentGetRes()));
        });

        return feedList;
    }


    //select 2번
    public List<FeedGetRes> getFeedList5(FeedGetReq p) {
        List<FeedGetRes> list = new ArrayList<>(p.getSize());

        //SELECT (1): feed + feed_pic
        List<FeedAndPicDto> feedAndPicDtoList = feedMapper.selFeedWithPicList(p);
        List<Long> feedIds = new ArrayList<>(list.size());

        FeedGetRes beforeFeedGetRes = new FeedGetRes();
        for(FeedAndPicDto feedAndPicDto : feedAndPicDtoList) {
            if(beforeFeedGetRes.getFeedId() != feedAndPicDto.getFeedId()) {
                feedIds.add(feedAndPicDto.getFeedId());

                beforeFeedGetRes = new FeedGetRes();
                beforeFeedGetRes.setPics(new ArrayList<>(3));
                list.add(beforeFeedGetRes);
                beforeFeedGetRes.setFeedId(feedAndPicDto.getFeedId());
                beforeFeedGetRes.setContents(feedAndPicDto.getContents());
                beforeFeedGetRes.setLocation(feedAndPicDto.getLocation());
                beforeFeedGetRes.setCreatedAt(feedAndPicDto.getCreatedAt());
                beforeFeedGetRes.setWriterUserId(feedAndPicDto.getWriterUserId());
                beforeFeedGetRes.setWriterNm(feedAndPicDto.getWriterNm());
                beforeFeedGetRes.setWriterPic(feedAndPicDto.getWriterPic());
                beforeFeedGetRes.setIsLike(feedAndPicDto.getIsLike());
            }
            beforeFeedGetRes.getPics().add(feedAndPicDto.getPic());
        }

        //SELECT (2): feed_comment
        List<FeedCommentDto> feedCommentList = feedCommentMapper.selFeedCommentListByFeedIdsLimit4Ver2(feedIds);
        Map<Long, FeedCommentGetRes> commentHashMap = new HashMap<>();
        for(FeedCommentDto item : feedCommentList) {
            long feedId = item.getFeedId();
            if(!commentHashMap.containsKey(feedId)) {
                FeedCommentGetRes feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>(4));
                commentHashMap.put(feedId, feedCommentGetRes);
            }
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(feedId);
            feedCommentGetRes.getCommentList().add(item);
        }
        for(FeedGetRes res : list) {
            FeedCommentGetRes feedCommentGetRes = commentHashMap.get(res.getFeedId());

            if(feedCommentGetRes == null) { //댓글이 하나도 없었던 피드인 경우
                feedCommentGetRes = new FeedCommentGetRes();
                feedCommentGetRes.setCommentList(new ArrayList<>());
            } else if (feedCommentGetRes.getCommentList().size() == 4) {
                feedCommentGetRes.setMoreComment(true);
                feedCommentGetRes.getCommentList().remove(feedCommentGetRes.getCommentList().size() - 1);
            }
            res.setComment(feedCommentGetRes);
        }

        return list;
    }


    public List<FeedGetRes> getFeedList6(FeedGetReq p) {
        // 데이터 매퍼를 호출하여 DTO 리스트를 가져옵니다.
        List<FeedWithPicCommentDto> dtoList = feedMapper.selFeedWithPicAndCommentLimit4List(p);

        // Null 체크: 데이터가 없을 경우 빈 리스트를 반환
        if (dtoList == null || dtoList.isEmpty()) {
            return new ArrayList<>();
        }

        // DTO 리스트를 FeedGetRes 리스트로 변환
        List<FeedGetRes> resList = new ArrayList<>();
        for (FeedWithPicCommentDto dto : dtoList) {
            FeedGetRes feedGetRes = new FeedGetRes(dto);
            resList.add(feedGetRes);
        }

        // 변환된 리스트를 반환
        return resList;
    }
}




