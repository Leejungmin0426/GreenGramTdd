package com.green.greengram.feed;

import com.green.greengram.common.model.MyFileUtils;
import com.green.greengram.common.exception.CustomException;
import com.green.greengram.config.security.AuthenticationFacade;
import com.green.greengram.feed.comment.FeedCommentMapper;
import com.green.greengram.feed.model.FeedPostReq;
import com.green.greengram.feed.model.FeedPostRes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {
    @Mock FeedMapper feedMapper;
    @Mock FeedPicMapper feedPicMapper;
    @Mock FeedCommentMapper feedCommentMapper;
    @Mock MyFileUtils myFileUtils;
    @Mock AuthenticationFacade authenticationFacade;
    @InjectMocks FeedService feedService;

    final long FEED_ID_10 = 10L;
    final long SIGNED_USER_ID = 3L;
    final String LOCATION = "테스트 위치";

    @Test
    @DisplayName("Insert시 영향받은 행이 0일 때, 예외 발생")
    void postFeedInsRows0ThrowException() {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).willReturn(0);

        FeedPostReq actualParam = new FeedPostReq();
        actualParam.setLocation(LOCATION);

        assertThrows(CustomException.class
                , () -> feedService.postFeed(null, actualParam)
        );

    }




    @Test
    @DisplayName("MyFileUtils의 transferTo 호출시 잘못된 경로면, 예외 발생")
    void test2() throws Exception {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).will(invocation -> {
            FeedPostReq invocationParam = (FeedPostReq) invocation.getArgument(0);
            invocationParam.setFeedId(FEED_ID_10);
            return 1;
        });
        final String SAVED_PIC_NAME_1 = "abc.jpg";
        MultipartFile mpf1 = new MockMultipartFile("pics", "test1.txt", "text/plain", "This is test1 file".getBytes());
        given(myFileUtils.makeRandomFileName(mpf1)).willReturn(SAVED_PIC_NAME_1);
        final String UPLOAD_PATH = "/home/download";
        given(myFileUtils.getUploadPath()).willReturn(UPLOAD_PATH);

        String expectedMiddlePath = String.format("feed/%d", FEED_ID_10);
        String givenFilePath1 = String.format("%s/%s", expectedMiddlePath, SAVED_PIC_NAME_1);

        given(myFileUtils.makeRandomFileName(mpf1)).willReturn(SAVED_PIC_NAME_1);
        doAnswer(invoctaion -> {
            throw new IOException();
        }).when(myFileUtils).transferTo(mpf1, givenFilePath1);

        assertAll(
                () -> {
                    List<MultipartFile> pics = new ArrayList<>(1);
                    pics.add(mpf1);
                    FeedPostReq actualParam = new FeedPostReq();
                    actualParam.setLocation(LOCATION);
                    assertThrows(CustomException.class, () -> feedService.postFeed(pics, actualParam));
                }
                , () -> verify(myFileUtils).makeFolders(expectedMiddlePath)
                , () -> {
                    String expectedDelFolderPath = String.format("%s/%s", UPLOAD_PATH, expectedMiddlePath);
                    verify(myFileUtils).deleteFolder(expectedDelFolderPath, true);
                }
        );
    }


    @Test
    @DisplayName("test2과 같은 맥락. 파일을 2개로 테스트 함.")
    void test2_1() throws Exception {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).will(invocation -> {
            FeedPostReq invocationParam = (FeedPostReq) invocation.getArgument(0);
            invocationParam.setFeedId(FEED_ID_10);
            return 1;
        });
        final String SAVED_PIC_NAME_1 = "abc.jpg";
        final String SAVED_PIC_NAME_2 = "def.jpg";
        MultipartFile mpf1 = new MockMultipartFile("pics", "test1.txt", "text/plain", "This is test1 file".getBytes());
        MultipartFile mpf2 = new MockMultipartFile("pics", "test2.txt", "text/plain", "This is test2 file".getBytes());
        given(myFileUtils.makeRandomFileName(mpf1)).willReturn(SAVED_PIC_NAME_1);
        given(myFileUtils.makeRandomFileName(mpf2)).willReturn(SAVED_PIC_NAME_2);

        String expectedMiddlePath = String.format("feed/%d", FEED_ID_10);
        String givenFilePath1 = String.format("%s/%s", expectedMiddlePath, SAVED_PIC_NAME_1);
        String givenFilePath2 = String.format("%s/%s", expectedMiddlePath, SAVED_PIC_NAME_2);

        doAnswer(invoctaion -> {
            return null;
        }).when(myFileUtils).transferTo(mpf1, givenFilePath1);

        doAnswer(invoctaion -> {
            throw new IOException();
        }).when(myFileUtils).transferTo(mpf2, givenFilePath2);

        List<MultipartFile> pics = new ArrayList<>(2);
        pics.add(mpf1);
        pics.add(mpf2);

        FeedPostReq actualParam = new FeedPostReq();
        actualParam.setLocation(LOCATION);
        assertThrows(CustomException.class, () -> feedService.postFeed(pics, actualParam));
        verify(myFileUtils).makeFolders(expectedMiddlePath); //verify는 메소드가 원하는 파라미터로 호출되었는지 확인할 수 있다.
    }


    @Test
    void test3() {
        given(authenticationFacade.getSignedUserId()).willReturn(SIGNED_USER_ID);

        FeedPostReq givenParam = new FeedPostReq();
        givenParam.setWriterUserId(SIGNED_USER_ID);
        givenParam.setLocation(LOCATION);
        given(feedMapper.insFeed(givenParam)).willReturn(0);

        FeedPostReq actualParam = new FeedPostReq();
        actualParam.setLocation(LOCATION);

        given(feedMapper.insFeed(givenParam)).will(invocation -> {
            FeedPostReq invocationParam = (FeedPostReq) invocation.getArgument(0);
            invocationParam.setFeedId(FEED_ID_10);
            return 1;
        });

    }


}