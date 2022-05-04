package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.img.ImgDto;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.timeConversion.MessageTimeConversion;
import com.hanghae99.finalproject.timeConversion.TimeConversion;
import com.hanghae99.finalproject.user.dto.AcceptedDto;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import com.hanghae99.finalproject.user.model.UserPortfolioImg;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.repository.UserApplyRepository;
import com.hanghae99.finalproject.user.repository.UserPortfolioImgRepository;
import com.hanghae99.finalproject.img.AwsS3UploadService;
import com.hanghae99.finalproject.img.FileUploadService;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final UserPortfolioImgRepository userPortfolioImgRepository;
    private final AwsS3UploadService s3UploadService;
    private final UserApplyRepository userApplyRepository;
    private final PostRepository postRepository;

    //마이페이지의 정보를 반환
    @Transactional(readOnly = true)
    public MyPageDto.ResponseDto findUserPage(Long userId) {
        // 나 자신 or상대 유저의 pk를 받아와 존재여부 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        // 유저 정보 조회 시 포트폴리오 이미지가 존재한다면 key/value 값으로 map 에 담아준다.
        Map<Long, String> userPortfolio = new HashMap<>();
        // 키와 밸류가 한쌍인 값들을 list에 담는다.
        List<Map<Long, String>> userPortfolioImgList = new ArrayList<>();
        //유저의 포트폴리오 이미지 리스트에서
        if (!user.getUserPortfolioImgList().isEmpty()) {
            // Map에 id , url을 한쌍으로 리스트 타입으로 담아준다.
            for (UserPortfolioImg portfolioImgList : user.getUserPortfolioImgList()) {
                userPortfolio.put(portfolioImgList.getId(), portfolioImgList.getPortfolioImgUrl());
                userPortfolioImgList.add(userPortfolio);
            }
            //List{{"1":"1.png"},{"2":"2.png"}}

        } else {

            userPortfolio.put(0L, "나의 포트폴리오 이미지를 넣어주세요.");
            userPortfolioImgList.add(userPortfolio);
        }


        // 닉네임/프로필 이미지/자기소개/ 등록한 포폴 이미지/ 내가 올린 글 목록
        return MyPageDto.ResponseDto.builder()
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg()) // default or 수정 이미지
                .intro(user.getIntro()) // default 값 or 수정 소개글
                .portfolioLink(user.getPortfolioLink())
                .userPortfolioImgList(userPortfolioImgList)
                .projectCount(user.getProjectCount()) //연관관계 메서드 설정 필요
                .likeCount(user.getLikeCount())
                .build();
    }

    //마이페이지 수정
    public void modifyUserInfo(Long userId, MyPageDto.RequestDto requestDto, List<MultipartFile> imgs,UserDetailsImpl userDetails) throws IOException{
        log.info("이미지={}",imgs);
        // '수정'에 접근한 유저의 정보를 불러온다.
        User user = userRepository.findById(userId).orElseThrow(
               () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        //접근한 유저정보와 토큰의 유저정보를 비교하여 일치할 때 유저 수정권한을 가능하게 한다.
        if(!user.equals(userDetails.getUser())){
            throw new PrivateException(ErrorCode.USER_UPDATE_WRONG_ACCESS);
        }
        //User 에 들어있는 이미지 정보들을 가져온다.
        List<UserPortfolioImg> portfolioImgList = user.getUserPortfolioImgList();


        // 현재 유저가 가지고 있는 사진들을 전체 roof
        for(UserPortfolioImg userPortfolioImg : portfolioImgList){
            // requestDto 에서 요청[삭제] url을 받아온다.
            for(ImgUrlDto imgUrlDto : requestDto.getCurrentImgUrl()){
                // 현재 유저에게 저장된 url과 삭제 요청받은 url 이 같다면
                if(userPortfolioImg.getPortfolioImgUrl().equals(imgUrlDto.getImgUrl())){
                    //s3에서 삭제
                    s3UploadService.deleteFile(userPortfolioImg.getPortfolioImgName());
                    //해당 유저와 연관관계가 맺어진 포트폴리오 이미지 레포에서도 해당 이미지들을 지운다.
                    userPortfolioImgRepository.deleteById(userPortfolioImg.getId());
                    // 삭제요청된 리스트들을 담아준다.
                }
            }
        }
        // 현재 변경 요청된 url이 해당 유저의 포폴 이미지에서 사라짐
        // 새롭게 받아온 이미지들을 해당 유저에게 전달해야함.

        //이미지가 Null 값이 아니라면
        if(imgs != null){
            for(MultipartFile img : imgs){
                log.info("이미지 존재유무={}",imgs.isEmpty());
                if(!imgs.isEmpty()){
                    // 파일 name과 url을 dto에 빌드
                    ImgDto imgDto = fileUploadService.uploadImage(img);
                    // 새로운 이미지를 저장 -> 유저와 연관관계
                    UserPortfolioImg userPortfolioImg = UserPortfolioImg.builder()
                            .user(user)
                            .portfolioImgName(imgDto.getImgName())
                            .portfolioImgUrl(imgDto.getImgUrl())
                            .build();
                    userPortfolioImgRepository.save(userPortfolioImg);
                }

            }
        }
        // 영속 컨텍스트에 저장된 값을 유저 pk로 찾아와
        List<UserPortfolioImg> userPortfolioImgList = userPortfolioImgRepository.findAllByUserId(user.getId());
        // 업데이트 한다.
        user.updateInfo(requestDto,userPortfolioImgList);


    }

    // 마이페이지 내의 신청중 리스트 찾아오기 .
    @Transactional(readOnly = true)
    public List<MyPageDto.AppliedResponseDto> responseAppliedList(UserDetailsImpl userDetails) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.AppliedResponseDto> appliedResponseDtoList = new ArrayList<>();

        // userPK -> 내가 지원한 모집글을 찾아온다.
        User user = userDetails.getUser();
        // 현재 유저가 참여[신청]하고 있는 게시글에 포함된 Apply정보를 전부 찾아온다.
        List<UserApply> userApplyList= userApplyRepository.findUserApplyByUser(user);
        // list {userApply1[postPk, userPk], userApply 2, }

        // Apply pk 를 활용해서 현재 참여하고 있는 포스트 글을 가져온다 .

        for(UserApply userApply :userApplyList){
            //반복 roof ->  userApply -> 참여한 게시글 pk 를 통해 post 를 받아온다.
            Post post = userApply.getPost(); // 현재 유저가 참여한 게시글 post
            //post 에서 필요한 내용들을 빌더
            MyPageDto.AppliedResponseDto appliedResponseDto = MyPageDto.AppliedResponseDto.builder()
                    .userId(post.getUser().getId())
                    .postId(post.getId())
                    .nickname(post.getUser().getNickname())
                    .title(post.getTitle())
                    .createAt(TimeConversion.timeConversion(post.getCreateAt()))
                    .build();
            //리스트에 담아준다
            appliedResponseDtoList.add(appliedResponseDto);
        }

        return appliedResponseDtoList;

    }
    //마이페이지 내의 모집중 리스트를 찾아온다.
    @Transactional(readOnly = true)
    public List<MyPageDto.RecruitResponseDto> responsePostRecruitList(Long userId) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitResponseDto> recruitResponseDtosList = new ArrayList<>();

        //현재 유저 페이지 pk를 받아와서 유저 정보조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        //현재 유저가 작성한 작성글을 찾아온다.
        List<Post> findPostsByuser = postRepository.findPostsByUser(user);


        for(Post findPosts : findPostsByuser){
            MyPageDto.RecruitResponseDto recruitResponseDto = MyPageDto.RecruitResponseDto.builder()
                    .userId(user.getId())
                    .postId(findPosts.getId())
                    .title(findPosts.getTitle())
                    .nickname(user.getNickname())
                    .createAt(TimeConversion.timeConversion(findPosts.getCreateAt()))
                    .build();

            recruitResponseDtosList.add(recruitResponseDto);
        }

        return recruitResponseDtosList;
    }

    //postPk를 받아와 해당 게시글에 '신청하기' 를 한 유저의 정보를 담아서 전달
    public List<MyPageDto.ApplyUserList> responseApplyMyPostUserList(Long postId,int isAccecpted) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.ApplyUserList> applyUserListByMyPost = new ArrayList<>();

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        //해당 모집글에 접근한 유저 정보들을 불러온다.
        List<UserApply> userApplyLists = post.getUserApplyList();


        //유저 정보들을 요청 response 값에 빌딩
        for (UserApply AppliedList : userApplyLists) {
            if (AppliedList.getIsAccepted() == isAccecpted) {
                MyPageDto.ApplyUserList applyUserList = MyPageDto.ApplyUserList.builder()
                        .post(post) // 해당 포스트 값에 담긴 정보 : ex )
                        .userId(AppliedList.getUser().getId()) // 전달자
                        .nickname(AppliedList.getUser().getNickname()) // 전달자 닉네임
                        .message(AppliedList.getMessage()) // 전달 메시지
                        .applyMajor(AppliedList.getApplyMajor()) // 지원한 전공
                        .AcceptedStatus(AppliedList.getIsAccepted()) //default -> false
                        .build();
                applyUserListByMyPost.add(applyUserList);
            }
        }


        return applyUserListByMyPost;

    }

    // 신청한 모집글의 유저 1이 현재 게시글 pk 와 자신의 유저 pk 를 전달
    public void modifyAcceptedStatus(AcceptedDto acceptedDto) {

        int isAccepted = 1;
        Post post = postRepository.findById(acceptedDto.getPostId()).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );
        User user = userRepository.findById(acceptedDto.getUserId()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        UserApply userApply = userApplyRepository.findUserApplyByUserAndPost(user, post).orElseThrow(
                () -> new IllegalArgumentException("신청자가 존재하지 않습니다.")
        );

        userApply.modifyAcceptedStatus(isAccepted);

    }
}


































