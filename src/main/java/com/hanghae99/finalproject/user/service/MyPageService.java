package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.img.*;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.timeConversion.TimeConversion;
import com.hanghae99.finalproject.user.dto.AcceptedDto;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.user.model.*;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.user.repository.*;
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
public class MyPageService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final UserPortfolioImgRepository userPortfolioImgRepository;
    private final AwsS3UploadService s3UploadService;
    private final UserApplyRepository userApplyRepository;
    private final PostRepository postRepository;
    private final UserRateRepository userRateRepository;
    private final MajorRepository majorRepository;

    //마이페이지의 정보를 반환
    @Transactional
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
            //List[{"1":"1.png"},{"2":"2.png"}]

        }
        // 닉네임/프로필 이미지/자기소개/ 등록한 포폴 이미지/ 내가 올린 글 목록
        return MyPageDto.ResponseDto.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg()) // default or 수정 이미지
                .intro(user.getIntro()) // default 값 or 수정 소개글
                .portfolioLink(user.getPortfolioLink())
                .major(user.getMajor())
                .userPortfolioImgList(userPortfolioImgList)
                .projectCount(user.getProjectCount()) //연관관계 메서드 설정 필요
                .likeCount(user.getLikeCount())
                .build();
    }

    //마이페이지 수정
    @Transactional
    public void modifyUserInfo(Long userId, MyPageDto.RequestDto requestDto, List<MultipartFile> imgs,UserDetailsImpl userDetails) throws IOException{
        log.info("이미지={}",imgs);
        // '수정'에 접근한 유저의 정보를 불러온다.
        User user = userRepository.findById(userId).orElseThrow(
               () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        //접근한 유저정보와 토큰의 유저정보를 비교하여 일치할 때 유저 수정권한을 가능하게 한다.
//        if(!user.equals(userDetails.getUser())){
//            throw new PrivateException(ErrorCode.USER_UPDATE_WRONG_ACCESS);
//        }


        // 현재 유저가 가지고 있는 사진들을 전체 roof
        //User 에 들어있는 이미지 정보들을 가져온다.
        List<UserPortfolioImg> portfolioImgList = user.getUserPortfolioImgList();

        List<UserPortfolioImg> removeImgList = new ArrayList<>();
        List<ImgDto> imgDtoList = new ArrayList<>();

        for (UserPortfolioImg userPortfolioImg : portfolioImgList) {
            // requestDto 에서 요청[삭제] url을 받아온다.
            for (ImgUrlDto imgUrlDto : requestDto.getCurrentImgUrl()) {
                // 현재 유저에게 저장된 url과 삭제 요청받은 url 이 같다면
                if (userPortfolioImg.getPortfolioImgUrl().equals(imgUrlDto.getImgUrl())) {
                    //s3에서 삭제
                    s3UploadService.deleteFile(userPortfolioImg.getPortfolioImgName());
                    //해당 유저와 연관관계가 맺어진 포트폴리오 이미지 레포에서도 해당 이미지들을 지운다.
                    userPortfolioImgRepository.deleteById(userPortfolioImg.getId());
                    removeImgList.add(userPortfolioImg);
                }
            }
        }

        // / removeImgList에 담긴 수정 이미지 찾아온 portfolioImgList 에서 제거
        for (UserPortfolioImg userPortfolioImg : removeImgList) {
            portfolioImgList.remove(userPortfolioImg);
        }


        //이미지가 Null 값이 아니라면
        if(imgs != null){
            for(MultipartFile img : imgs){
                if(!imgs.isEmpty()){
                    // 파일 name과 url을 dto에 빌드
                    ImgDto imgDto = fileUploadService.uploadImage(img);
                    imgDtoList.add(imgDto);
                }
            }
        }

        updateImgParser(portfolioImgList,imgDtoList);
        user.updateInfo(requestDto,portfolioImgList);

    }

    private void updateImgParser(List<UserPortfolioImg> imgList, List<ImgDto> imgDtoList) {
        for (ImgDto imgDto : imgDtoList) {
            UserPortfolioImg img = UserPortfolioImg.builder()
                    .portfolioImgName(imgDto.getImgName())
                    .portfolioImgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
        }
    }


    // 마이페이지 내의 신청중 리스트 찾아오기 .
    @Transactional
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
                    .createAt(TimeConversion.timeConversion(post.getCreatedAt()))
                    .build();
            //리스트에 담아준다
            appliedResponseDtoList.add(appliedResponseDto);
        }

        return appliedResponseDtoList;

    }
    //마이페이지 내의 모집중 리스트를 찾아온다.
    @Transactional
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
                    .createAt(TimeConversion.timeConversion(findPosts.getCreatedAt()))
                    .build();

            recruitResponseDtosList.add(recruitResponseDto);
        }

        return recruitResponseDtosList;
    }

    //postPk를 받아와 해당 게시글에 '신청하기' 를 한 유저의 정보를 담아서 전달
    @Transactional
    public List<MyPageDto.ApplyUserList> responseApplyMyPostUserList(Long postId,int isAccecpted) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.ApplyUserList> applyUserListByMyPost = new ArrayList<>();

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );
        //entity to dto
        MyPageDto.ResponseEntityToPost responseEntityToPost = new MyPageDto.ResponseEntityToPost(post);
        //해당 모집글에 접근한 유저 정보들을 불러온다.
        List<UserApply> userApplyLists = post.getUserApplyList();
        //유저 정보들을 요청 response 값에 빌딩
        for (UserApply AppliedList : userApplyLists) {
            // 받아온 reqeust 요청 : 0 ==  기본값 : 0 [수락전]
            // reqeust 요청 : 1 == 0 인 상태의 값만 보여준다.
            if (isAccecpted == AppliedList.getIsAccepted()) {
                MyPageDto.ApplyUserList applyUserList = MyPageDto.ApplyUserList.builder()
                        .post(responseEntityToPost) // 해당 포스트 값에 담긴 정보 : ex )
                        .userId(AppliedList.getUser().getId()) // 전달자
                        .nickname(AppliedList.getUser().getNickname()) // 전달자 닉네임
                        .message(AppliedList.getMessage()) // 전달 메시지
                        .applyMajor(AppliedList.getApplyMajor()) // 지원한 전공
                        .AcceptedStatus(AppliedList.getIsAccepted()) //default -> 0
                        .build();
                applyUserListByMyPost.add(applyUserList);
            }
        }
        //만약 0인 상태가 없다면 ? 1인 상태가 없다면 ?

        return applyUserListByMyPost;

    }

    // 신청한 모집글의 유저 1이 현재 게시글 pk 와 자신의 유저 pk 를 전달
    // '요청 수락' 시 acceted 가 0 -> 1 로 변경
    @Transactional
    public void modifyAcceptedStatus(AcceptedDto acceptedDto) {


        Post post = postRepository.findById(acceptedDto.getPostId()).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );
        User user = userRepository.findById(acceptedDto.getUserId()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        UserApply userApply = userApplyRepository.findUserApplyByUserAndPost(user, post).orElseThrow(
                () -> new IllegalArgumentException("신청자가 존재하지 않습니다.")
        );

        //수락을 받는 유저가 참여한 포스트에는 요청 전공들이 존재하고
        List<Major> majorList = post.getMajorList();
        //userApply 지원자는 그 중의 하나를 선택한다.

        for(Major major:majorList){
            if(major.getMajorName().equals(userApply.getApplyMajor())){
                major.updateApplyCount();
            }
        }

        int isAccepted = 1;

        userApply.modifyAcceptedStatus(isAccepted);

    }

    //모집 마감 목록 조회
    //신청한 글들과 모집한 글들을 전부 찾아와 스테이터스가 모집마감인 상태의 글들을 반환해준다.
    @Transactional
    public List<MyPageDto.RecruitOverList> findRecruitOverList(Long userId) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitOverList> recruitOverLists = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 모집글 list를 전부 가져온다
        List<Post> recruitPostLists = postRepository.findPostsByUser(user);

        //유저가 신청한 모집글 list 를 전부 가져온다.
        List<UserApply> userApplyLists = userApplyRepository.findUserApplyByUser(user);

        // 유저가 모집한 글들의 리스트 -> roof
        for(Post StatusOverByPost :recruitPostLists){
            if(StatusOverByPost.getCurrentStatus() == CurrentStatus.RECRUITING_COMPLETE){
                MyPageDto.RecruitOverList recruitOverList = MyPageDto.RecruitOverList.builder()
                        .postId(StatusOverByPost.getId())
                        .nickname(StatusOverByPost.getUser().getNickname())
                        .createdAt(TimeConversion.timeConversion(StatusOverByPost.getCreatedAt()))
                        .userApplyList(StatusOverByPost.getUserApplyList())
                        .build();
                recruitOverLists.add(recruitOverList);
            }
        }
        // 연관관계를 통해 신청자가 신청한 게시글들을 가져온다 .
        for(UserApply PostByAppliedUser :userApplyLists){
            if(PostByAppliedUser.getPost().getCurrentStatus() == CurrentStatus.RECRUITING_COMPLETE){
                MyPageDto.RecruitOverList recruitOverList = MyPageDto.RecruitOverList.builder()
                        .postId(PostByAppliedUser.getPost().getId())
                        .nickname(PostByAppliedUser.getUser().getNickname())
                        .createdAt(TimeConversion.timeConversion(PostByAppliedUser.getPost().getCreatedAt()))
                        .userApplyList(PostByAppliedUser.getPost().getUserApplyList())
                        .build();
                recruitOverLists.add(recruitOverList);
            }
        }

        return recruitOverLists;
    }

    //모집 마감 list에서 특정 게시글 pk에 접근하여 참여한 user정보들을 반환받아온다.
    @Transactional
    public List<MyPageDto.RecruitUserList> findRecruitUserList(Long postId) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitUserList> recruitUserLists = new ArrayList<>();

        // 팀원 리뷰 클릭 시 모집마감 list에서 postId를 전달  EX: 1
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );
        // 1번 모집글에 참여한 유저 정보들을 가져온다.
        List<UserApply> userApplyList= post.getUserApplyList();

        // roof -> 필요한 정보들을 가공하여 list에 담아준다.
        // 조건을 달아준다. -> 별점을 받은 인원은 안보이게끔!!
        for(UserApply userApply :userApplyList){
            if(userApply.getUser().getRateStatus() == null) {
                MyPageDto.RecruitUserList recruitUserList = MyPageDto.RecruitUserList.builder()
                        .userId(userApply.getUser().getId())
                        .nickname(userApply.getUser().getNickname())
                        .profileImg(userApply.getUser().getProfileImg())
                        .build();
                recruitUserLists.add(recruitUserList);
            }
        }

        return recruitUserLists;
    }

    //모집 마감 list에서 특정 게시글 pk에 접근해서 가져온 유저 list에서 특정 유저에게 평점을 준다.
    @Transactional
    public void EvaluationUser(MyPageDto.RequestUserRate requestUserRate,UserDetailsImpl userDetails) {
            //담겨온 유저 정보[평가를받는 ]를 조회한다.
            User receiver = userRepository.findById(requestUserRate.getReceiverId()).orElseThrow(
                    () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
            );

            //어떤 모집글에서의 평가가 이루어졌는지 확인
            Post post = postRepository.findById(requestUserRate.getPostId()).orElseThrow(
                    () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
            );

            UserRate userRate = UserRate.builder()
                    .sender(userDetails.getUser())
                    .receiver(receiver)
                    .post(post)
                    .build();

            receiver.updateRateStatus(requestUserRate.getPoint());

    }
}


































