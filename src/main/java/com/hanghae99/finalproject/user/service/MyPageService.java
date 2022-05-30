package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.img.AwsS3UploadService;
import com.hanghae99.finalproject.img.FileUploadService;
import com.hanghae99.finalproject.img.ImgDto;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.mail.dto.MailDto;
import com.hanghae99.finalproject.mail.service.MailService;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.sse.model.NotificationType;
import com.hanghae99.finalproject.sse.service.NotificationService;
import com.hanghae99.finalproject.user.dto.AcceptedDto;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import com.hanghae99.finalproject.user.dto.RejectDto;
import com.hanghae99.finalproject.user.model.*;
import com.hanghae99.finalproject.user.repository.UserApplyRepository;
import com.hanghae99.finalproject.user.repository.UserPortfolioImgRepository;
import com.hanghae99.finalproject.user.repository.UserRateRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {
    //신청중 / 모집중 / 모집완료 페이지네이션 처리 필요

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final UserPortfolioImgRepository userPortfolioImgRepository;
    private final AwsS3UploadService s3UploadService;
    private final UserApplyRepository userApplyRepository;
    private final PostRepository postRepository;
    private final MailService mailService;
    private final NotificationService notificationService;
    private final UserRateRepository userRateRepository;
    //마이페이지의 정보를 반환
    @Transactional
    public MyPageDto.ResponseDto findUserPage(Long userId) {
        // 나 자신 or상대 유저의 pk를 받아와 존재여부 확인
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

       List<String> imgUrlDtoList = new ArrayList<>();

        //유저의 포트폴리오 이미지 리스트에서
        if (!user.getUserPortfolioImgList().isEmpty()) {
            for (UserPortfolioImg portfolioImgList : user.getUserPortfolioImgList()) {
                imgUrlDtoList.add(portfolioImgList.getPortfolioImgUrl());
            }
        }

        //이미지가 없다면 ,Default 값 전달해야함. 디자이너분들과 상의 후 변경필요
        //리스트값이 비었다면 프론트단에서 디폴트 이미지를 보여주기로 협의



        // 닉네임/프로필 이미지/자기소개/ 등록한 포폴 이미지/ 내가 올린 글 목록
        return MyPageDto.ResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImg(user.getProfileImg()) // default or 수정 이미지
                .intro(user.getIntro()) // default 값 or 수정 소개글
                .portfolioLink(user.getPortfolioLink())
                .major(user.getMajor())

                .userPortfolioImgList(imgUrlDtoList)
                .userRateTotal(user.getUserRateCount())
                    //해당 유저가 받은 유저
                    // primitive 타입은 - > null 값을 허용안함.  -> default 값을 0으로 설정해야함.
                    // 1 . nullable이 가능한 rapper 사용 ,
                    // 2.
                .projectCount(user.getProjectCount()) //연관관계 메서드 설정 필요
                .likeCount(user.getLikeCount())
                .build();
    }

    //마이페이지 수정
    @Transactional
    public void modifyUserInfo(Long userId, MyPageDto.RequestDto requestDto, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("이미지={}", imgs);
        // '수정'에 접근한 유저의 정보를 불러온다.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
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
        if (imgs != null) {
            for (MultipartFile img : imgs) {
                if (!imgs.isEmpty()) {
                    // 파일 name과 url을 dto에 빌드
                    ImgDto imgDto = fileUploadService.uploadImage(img, "user");
                    imgDtoList.add(imgDto);
                }
            }
        }
        updateImgParser(portfolioImgList, imgDtoList);
        user.updateInfo(requestDto, portfolioImgList);
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
    public List<MyPageDto.AppliedResponseDto> responseAppliedList(Long userId) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.AppliedResponseDto> appliedResponseDtoList = new ArrayList<>();

        // userPK -> 내가 지원한 모집글을 찾아온다.
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        // 현재 유저가 참여[신청]하고 있는 게시글에 포함된 Apply정보를 전부 찾아온다.
        List<UserApply> userApplyList = userApplyRepository.findUserApplyByUser(user);
        // list {userApply1[postPk, userPk], userApply 2, }

        // Apply pk 를 활용해서 현재 참여하고 있는 포스트 글을 가져온다 .

        for (UserApply userApply : userApplyList) {
            //반복 roof ->  userApply -> 참여한 게시글 pk 를 통해 post 를 받아온다.
            Post post = userApply.getPost(); // 현재 유저가 참여한 게시글 post
            // 게시글이 진행중 일 때만 신청중에 내용들이 나와야한다.
            if(post.getCurrentStatus() == CurrentStatus.ONGOING) {
                MyPageDto.AppliedResponseDto appliedResponseDto = MyPageDto.AppliedResponseDto.builder()
                        .userId(post.getUser().getId())
                        .postId(post.getId())
                        .nickname(post.getUser().getNickname())
                        .title(post.getTitle())
                        .status(post.getCurrentStatus())
                        .createdAt(post.getCreatedAt())
                        .build();
                //리스트에 담아준다
                appliedResponseDtoList.add(appliedResponseDto);
            }
        }
        return appliedResponseDtoList
                .stream()
                .sorted(Comparator.comparing(MyPageDto.AppliedResponseDto::getCreatedAt).reversed())
                .collect(Collectors.toList());


    }

    //마이페이지 내의 모집중 리스트를 찾아온다.
    @Transactional
    public List<MyPageDto.RecruitResponseDto> responsePostRecruitList(Long userId) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitResponseDto> recruitResponseDtosList = new ArrayList<>();

        //현재 유저 페이지 pk를 받아와서 유저 정보조회
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        //현재 유저가 작성한 작성글을 찾아온다.
        List<Post> findPostsByuser = postRepository.findPostsByUser(user);


        //모집중 리스트에는 현재 게시글 상태가 진행중이거나 , 마감인 상태의 모집글 리스트들이 보여야함.
        for (Post findPosts : findPostsByuser) {
            if(findPosts.getCurrentStatus() == CurrentStatus.ONGOING
                    || findPosts.getCurrentStatus() == CurrentStatus.RECRUITING_CLOSE) {
                MyPageDto.RecruitResponseDto recruitResponseDto = MyPageDto.RecruitResponseDto.builder()
                        .userId(user.getId())
                        .postId(findPosts.getId())
                        .title(findPosts.getTitle())
                        .nickname(user.getNickname())
                        .userApplyList(userApplyListToDtoList(findPosts.getUserApplyList()))
                        .createAt(findPosts.getCreatedAt())
                        .build();

                recruitResponseDtosList.add(recruitResponseDto);
            }
        }
        return recruitResponseDtosList
                .stream()
                .sorted(Comparator.comparing( MyPageDto.RecruitResponseDto::getCreateAt).reversed())
                .collect(Collectors.toList());
    }

    //userApplyList 무한참조 방지를 위해 DTO 타입으로 매핑 후 전달
    private List<MyPageDto.ResponseEntityToUserApply> userApplyListToDtoList(List<UserApply>userApplyList){

        List<MyPageDto.ResponseEntityToUserApply> resultList = new ArrayList<>();
        for(UserApply userApply : userApplyList){

            MyPageDto.ResponseEntityToUserApply responseEntityToUserApply= MyPageDto.ResponseEntityToUserApply.builder()
                    .id(userApply.getId())
                    .postId(userApply.getPost().getId())
                    .userId(userApply.getUser().getId())
                    .nickname(userApply.getUser().getNickname())
                    .profileImg(userApply.getUser().getProfileImg())
                    .applyMajor(userApply.getApplyMajor())
                    .message(userApply.getMessage())
                    .build();
            resultList.add(responseEntityToUserApply);
        }

        return resultList;
    }


    //postPk를 받아와 해당 게시글에 '신청하기' 를 한 유저의 정보를 담아서 전달
    //'모집글' 리스트에서 특정 모집글에서 '명단보기 ' 클릭 시...
    @Transactional
    public MyPageDto.ResponseEntityToDto responseApplyMyPostUserList(Long postId, int isAccecpted) {

        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.ApplyUserList> applyUserListByMyPost = new ArrayList<>();
        //post에 들어갈 MajorList entity -> dto
        List<MajorDto.ResponseDto> postMajorListDto = new ArrayList<>();

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        //entity to dto
        for (Major postMajor : post.getMajorList()) {
            MajorDto.ResponseDto majorListDto = new MajorDto.ResponseDto(postMajor);
            postMajorListDto.add(majorListDto);
        }
        //해당 모집글에 접근한 유저 정보들을 불러온다.
        List<UserApply> userApplyLists = post.getUserApplyList();

        // .postMajorList(postMajorListDto) << 어떻게줄까?
        //유저 정보들을 요청 response 값에 빌딩
        for (UserApply AppliedList : userApplyLists) {
            // 받아온 reqeust 요청 : 0 ==  기본값 : 0 [수락전]
            // reqeust 요청 : 1 == 0 인 상태의 값만 보여준다.
            if (isAccecpted == AppliedList.getIsAccepted()) {
                MyPageDto.ApplyUserList applyUserList = MyPageDto.ApplyUserList.builder()
                        .userId(AppliedList.getUser().getId()) // 전달자
                        .nickname(AppliedList.getUser().getNickname()) // 전달자 닉네임
                        .profileImg(AppliedList.getUser().getProfileImg()) // 유저의 프로필 이미지
                        .message(AppliedList.getMessage()) // 전달 메시지
                        .applyMajor(AppliedList.getApplyMajor()) // 지원한 전공
                        .AcceptedStatus(AppliedList.getIsAccepted()) //default -> 0
                        .projectCount(AppliedList.getUser().getProjectCount()) //해당 인원의 프로젝트 카운트수
                        .likePoint(AppliedList.getUser().getLikeCount()) // 지원자의 평점
                        .build();
                applyUserListByMyPost.add(applyUserList);
            }
        }
        // 무한참조 방지를 위해 entity 를 dto에 담아 전달
        return new MyPageDto.ResponseEntityToDto(postMajorListDto,applyUserListByMyPost);
    }

    // 신청한 모집글의 유저 1이 현재 게시글 pk 와 자신의 유저 pk 를 전달
    // '요청 수락' 시 acceted 가 0 -> 1 로 변경
    // 모집 전공 수가 충족된다면 해당 게시글 상태 변화

    @Transactional
    public void modifyAcceptedStatus(AcceptedDto acceptedDto) throws MessagingException {
        int isAccepted = 1;
        int totalSetCount=0;
        int totalApplyCount=0;

        Post post = postRepository.findById(acceptedDto.getPostId()).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        User user = userRepository.findById(acceptedDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        UserApply userApply = userApplyRepository.findUserApplyByUserAndPost(user, post).orElseThrow(
                () -> new CustomException(ErrorCode.APPLY_NOT_FOUND)
        );

        //수락을 받는 유저가 참여한 포스트에는 요청 전공들이 존재하고
        List<Major> majorList = post.getMajorList();
        //userApply 지원자는 그 중의 하나를 선택한다.

        // ex) SET : 2 / APPLY : 0 -> SET : 2 /APPLY : 1
        // ex) SET : 3 / APPLY : 0 -> SET : 3 /APPLY : 1
        for (Major major : majorList) {
            if (major.getMajorName().equals(userApply.getApplyMajor())) major.increaseApplyCount();
            if (major.getNumOfPeopleSet() < major.getNumOfPeopleApply()) throw new CustomException(ErrorCode.EXCEED_APPLY_USER_NUMBER);
            //구하는 전체 인원수를 쌓는다.    2 + 3..= 구하는 전공들의 전체 인원수
            totalSetCount += major.getNumOfPeopleSet();
            //현재 major의 총 지원자수를 초기화 시켜준다.
            totalApplyCount += major.getNumOfPeopleApply();
        }
        // 구하는 전체인원수와 지원한 인원수가 같다면 마감
        if(totalSetCount == totalApplyCount) post.updateStatus(CurrentStatus.RECRUITING_CLOSE);

        // 수락시 지원자에게 메일 발송(지원자가 이메일 인증 했을 경우만)
       if (user.getIsVerifiedEmail() != null) {
           mailService.acceptTeamMailBuilder(new MailDto(user, post));
       }
        //해당 댓글로 이동하는 url
        String Url = "https://www.everymohum.com/user/"+userApply.getUser().getId();
        //신청 수락 시 신청 유저에게 실시간 알림 전송 ,
        String content = userApply.getUser().getNickname()+"님! 프로젝트 매칭 알림이 도착했어요!";
        notificationService.send(userApply.getUser(),NotificationType.ACCEPT,content,Url);


        userApply.modifyAcceptedStatus(isAccepted);
       //수락 받은 user에게 알림
    }
    //신청한 모집글에서 유저명단 -> 해당 인원의 요청을 거절 시.
    //팀원명단에서 해당 인원을 강퇴 시 같이 사용 .
    @Transactional
    public void rejectUserApply(RejectDto rejectDto) throws MessagingException {
        Post post = postRepository.findById(rejectDto.getPostId()).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        User user = userRepository.findById(rejectDto.getUserId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        UserApply userApply = userApplyRepository.findUserApplyByUserAndPost(user, post).orElseThrow(
                () -> new CustomException(ErrorCode.APPLY_NOT_FOUND)
        );

        //해당 댓글로 이동하는 url
        String Url = "https://www.everymohum.com/user/"+userApply.getUser().getId();
        //신청 수락 시 신청 유저에게 실시간 알림 전송 ,

        if(userApply.getIsAccepted() == 0) {
            userApplyRepository.deleteById(userApply.getId());
            String content = userApply.getUser().getNickname()+"님! 프로젝트 매칭 실패 알림이 도착했어요!";
            notificationService.send(userApply.getUser(),NotificationType.REJECT,content,Url);
        }
        //isAccepted가 == 1 이라면[현재 팀원 목록에 존재하는 참가자]
        //해당 전공에서 지원수를 1 차감한다.
        if(userApply.getIsAccepted() == 1){
            List<Major> majorList = post.getMajorList();
            for(Major major : majorList){
                if (major.getMajorName().equals(userApply.getApplyMajor())) {
                    major.decreaseApplyCount();
                }
            }
            // 스테이터스는 진행중으로 유지한다.
            post.updateStatus(CurrentStatus.ONGOING);
            userApplyRepository.deleteById(userApply.getId());
            String content = userApply.getUser().getNickname()+"님! 프로젝트 하차 알림이 도착했어요!";
            notificationService.send(userApply.getUser(),NotificationType.REJECT,content,Url);
        }

        /*
         거절 or 강퇴시 지원자에게 메일 발송(지원자가 이메일 인증했을 경우만)
         1) 거절시 - userApply.getIsAccepted = 0일 때
         2) 강퇴시 - userApply.getIsAccepted = 1일 때
         */
        if (user.getIsVerifiedEmail() != null && userApply.getIsAccepted() == 0) {  // 1)
            mailService.rejectTeamMailBuilder(new MailDto(user, post));
        } else if (user.getIsVerifiedEmail() != null & userApply.getIsAccepted() == 1) {    // 2)
            mailService.forcedRejectTeamEmailBuilder(new MailDto(user, post));
        }

    }
    //모집 마감 목록 조회
    //신청한 글들과 모집한 글들을 전부 찾아와 스테이터스가 모집마감인 상태의 글들을 반환해준다.
    @Transactional
    public List<MyPageDto.RecruitOverList> findRecruitOverList(Long userId, UserDetailsImpl userDetails) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitOverList> recruitOverLists = new ArrayList<>();

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 모집글 list를 전부 가져온다
        List<Post> recruitPostLists = postRepository.findPostsByUser(user);


        // 모집 마감 시 해당 글에 속해있는 userlist에 acceted == 0 인 유저를 지운다.
        // 그럼 해당글에는 유저리스트의 상태가 1인 사람들만 존재한다.

        //유저가 신청한 모집글 list 를 전부 가져온다.
        // 가져올 때는 이미 유저 리스에는 수락이 신청된 인원만 존재한다.
        List<UserApply> userApplyLists = userApplyRepository.findUserApplyByUser(user);

        //userApplyList ->Dto파싱


        // 유저가 모집한 글들의 리스트 -> roof
        for (Post StatusOverByPost : recruitPostLists) {
            if (StatusOverByPost.getCurrentStatus() == CurrentStatus.RECRUITING_COMPLETE) {
                MyPageDto.RecruitOverList recruitOverList = MyPageDto.RecruitOverList.builder()
                        .postId(StatusOverByPost.getId())
                        .title(StatusOverByPost.getTitle())
                        .nickname(StatusOverByPost.getUser().getNickname())
                        .createdAt(StatusOverByPost.getCreatedAt())
                        .userApplyList(userApplyList(StatusOverByPost.getUserApplyList()))
                        .build();
                recruitOverLists.add(recruitOverList);
            }
        }


        // 연관관계를 통해 신청자가 신청한 게시글들을 가져온다 .
        for (UserApply PostByAppliedUser : userApplyLists) {
            if (PostByAppliedUser.getPost().getCurrentStatus() == CurrentStatus.RECRUITING_COMPLETE) {
                MyPageDto.RecruitOverList recruitOverList = MyPageDto.RecruitOverList.builder()
                        .postId(PostByAppliedUser.getPost().getId())
                        .title(PostByAppliedUser.getPost().getTitle())
                        .nickname(PostByAppliedUser.getUser().getNickname())
                        .createdAt(PostByAppliedUser.getPost().getCreatedAt())
                        .userApplyList(userApplyList(PostByAppliedUser.getPost().getUserApplyList()))
                        .build();
                recruitOverLists.add(recruitOverList);
            }
        }

        return recruitOverLists.stream()
                .sorted(Comparator.comparing(MyPageDto.RecruitOverList::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    // userApply 리스트들을 dtolist로 파싱
    private List<MyPageDto.ResponseEntityToUserApply> userApplyList(List<UserApply> userApplylist) {

        List<MyPageDto.ResponseEntityToUserApply> responseEntityToUserApplyList = new ArrayList<>();

        for (UserApply userApply : userApplylist) {
            if(userApply.getIsAccepted() == 1) {
                MyPageDto.ResponseEntityToUserApply responseEntityToUserApply = MyPageDto.ResponseEntityToUserApply.builder()
                        .id(userApply.getId())
                        .userId(userApply.getUser().getId())
                        .postId(userApply.getPost().getId())
                        .profileImg(userApply.getUser().getProfileImg())
                        .nickname(userApply.getUser().getNickname())
                        .message(userApply.getMessage())
                        .applyMajor(userApply.getApplyMajor())
                        .build();
                responseEntityToUserApplyList.add(responseEntityToUserApply);
            }
        }
        return responseEntityToUserApplyList;
    }


    //모집 마감 list에서 특정 게시글 pk에 접근하여 참여한 user정보들을 반환받아온다.
    @Transactional
    public MyPageDto.RecruitPostUser findRecruitUserList(Long postId,UserDetailsImpl userDetails) {
        //최종적으로 보낼 값들을 담아줄 list 선언
        List<MyPageDto.RecruitUserList> recruitUserLists = new ArrayList<>();

        User user = userDetails.getUser(); // 현재 로그인한 유저 ,
        // 팀원 리뷰 클릭 시 모집마감 list에서 postId를 전달  EX: 1
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
        //현재 모집글들에 참여한 userApplyList 를 불러옴 ,
        List<UserApply> userApplyList = post.getUserApplyList();

        // 참가자 리스트에는 모집글 유저의 정보가 없기에,
        // post에서 필요한 정보만 DTO에 빌딩
        MyPageDto.ResponsePostToUserApply postUser
                = MyPageDto.ResponsePostToUserApply.builder()
                .userId(post.getUser().getId())
                .nickname(post.getUser().getNickname())
                .profileImg(post.getUser().getProfileImg())
                .build();
        // 현재 모집글에 지원한 참가자들
        for (UserApply userApply : userApplyList) {
            // 지원자들 1, 2, 3 의 유저평가 정보 조회 -> 점수를 주는 이와 받는이를 기준으로 조회
            Optional<UserRate> userRate = userRateRepository.findUserRateByPostAndReceiverAndSender(post, userApply.getUser(), user);

            // 평점이 존재하지 않고 자기 자신은 보여야하지 않아야한다.
            if (!userRate.isPresent() && !Objects.equals(user.getId(), userApply.getUser().getId())
                    && userApply.getIsAccepted() == 1) {
                MyPageDto.RecruitUserList recruitUserList = MyPageDto.RecruitUserList.builder()
                        .userId(userApply.getUser().getId())
                        .nickname(userApply.getUser().getNickname())
                        .profileImg(userApply.getUser().getProfileImg())
                        .build();
                recruitUserLists.add(recruitUserList);
            }
        }
        // 필터링된 참가자들의 정보와 모집글을 게시한 유저의 정보
        Optional<UserRate> postUserRate = userRateRepository.findUserRateByPostAndReceiverAndSender(post, post.getUser(), user);
        if (!postUserRate.isPresent() && !Objects.equals(post.getUser().getId(), userDetails.getUser().getId())) {
            return new MyPageDto.RecruitPostUser(postUser, recruitUserLists);
            // 게시글 주인이 평점을 받았다면 필터링된 유저들의 정보만 내려준다.
        } else {
            return new MyPageDto.RecruitPostUser(recruitUserLists);
        }
    }

    //모집 마감 list에서 특정 게시글 pk에 접근해서 가져온 유저 list에서 특정 유저에게 평점을 준다.
    @Transactional
    public void EvaluationUser(MyPageDto.RequestUserRate requestUserRate, UserDetailsImpl userDetails) {
        //담겨온 유저 정보[평가를받는 ]를 조회한다.

        User receiver = userRepository.findById(requestUserRate.getReceiverId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO));
        //어떤 모집글에서의 평가가 이루어졌는지 확인
        Post post = postRepository.findById(requestUserRate.getPostId()).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));

        User sender = userDetails.getUser();

        UserRate userRate = UserRate.builder()
                .sender(sender)
                .receiver(receiver)
                .post(post) //현재 게시글
                .rateStatus(false) // 상태
                .ratePoint(requestUserRate.getPoint()) // 점수
                .build();

        userRateRepository.save(userRate);
        receiver.updateRateStatus(userRate);
        receiver.updateRateCount();

        String Url = "https://www.everymohum.com/user/"+receiver.getId();
        String content = receiver.getNickname()+"님! "+sender.getNickname()+"님이 평점을 남기셨어요!";
        notificationService.send(receiver,NotificationType.REVIEW,content,Url);

    }

}