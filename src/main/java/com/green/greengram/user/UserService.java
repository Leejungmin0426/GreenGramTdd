package com.green.greengram.user;

import com.green.greengram.common.model.MyFileUtils;
import com.green.greengram.feed.model.FeedPicDto;
import com.green.greengram.feed.model.FeedPostRes;
import com.green.greengram.user.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service

public class UserService {
    private final UserMapper mapper;
    private final MyFileUtils myFileUtils;


    public int insUser(UserSignUpReq p, MultipartFile pic) {

        String password = BCrypt.hashpw(p.getUpw(), BCrypt.gensalt());
        p.setUpw(password);
        String savedPicName = null;
        int result = mapper.insUser(p);

        if (pic != null) {
            savedPicName = myFileUtils.makeRandomFileName(pic);
        }
        p.setPic(savedPicName);

        if (pic == null) {
            return result;
        }

        // D:\2024-02\download\greengram_ver2/user/1/ahjsgdafsd.jpg
        String middleName = String.format("user/%s", p.getUserId());
        myFileUtils.makeFolders(middleName);

        try {
            myFileUtils.transferTo(pic, middleName + "/" + savedPicName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public UserSignInRes selUserByUid(UserSignInReq p) {
        UserSignInRes res = mapper.selUserByUid(p.getUid());
        if (res == null) {
            res = new UserSignInRes();
            res.setMessage("아이디를 확인해 주세요!");
            return res;
        }

        boolean trueId = BCrypt.checkpw(p.getUpw(), res.getUpw());
        if (!trueId) {
            res = new UserSignInRes();
            res.setMessage("비밀번호를 확인해 주세요!");
            return res;
        }
        res.setMessage("로그인 성공!");
        return res;
    }

    public UserInfoGetRes getUserInfo(UserInfoGetReq p) {
        return mapper.selUserInfo(p);
    }


    @Transactional
    public String patchUserPic(UserPicPatchReq p) {
        //저장할 파일명(랜덤한 파일명) 생성한다. 이때, 확장자는 오리지날 파일명과 일치하게 한다.
        String savedPicName = (p.getPic() != null ? myFileUtils.makeRandomFileName(p.getPic()) : null);

        //폴더 만들기 (최초에 프로필 사진이 없었다면 폴더가 없기 때문)
        String folerPath = String.format("user/%d", p.getSignedUserId());
        myFileUtils.makeFolders(folerPath);

        //기존 파일 삭제(방법 3가지 [1]: 폴더를 지운다. [2]select해서 기존 파일명을 얻어온다. [3]기존 파일명을 FE에서 받는다.)
        String deletePath = String.format("%s/user/%d", myFileUtils.getUploadPath(), p.getSignedUserId());
        myFileUtils.deleteFolder(deletePath, false);

        //DB에 튜플을 수정(Update)한다.
        p.setPicName(savedPicName);
        int result = mapper.updUserPic(p);

        if(p.getPic() == null) { return null; }
        //원하는 위치에 저장할 파일명으로 파일을 이동(transferTo)한다.
        String filePath = String.format("user/%d/%s", p.getSignedUserId(), savedPicName);

        try {
            myFileUtils.transferTo(p.getPic(), filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savedPicName;
    }
}
