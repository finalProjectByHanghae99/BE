package com.hanghae99.finalproject.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalproject.comment.dto.CommentResponseDto;
import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.comment.repository.CommentRepository;
import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.img.*;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import com.hanghae99.finalproject.user.model.UserStatus;
import com.hanghae99.finalproject.user.repository.MajorRepository;
import com.hanghae99.finalproject.user.repository.UserApplyRepository;
import com.hanghae99.finalproject.user.repository.UserRateRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.user.service.UserApplyService;
import com.hanghae99.finalproject.validator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ImgRepository imgRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final MajorRepository majorRepository;
    private final UserApplyRepository userApplyRepository;
    private final UserRateRepository userRateRepository;

    private final FileUploadService fileUploadService;
    private final AwsS3UploadService s3UploadService;
    private final UserApplyService userApplyService;

    // post 등록
    @Transactional
    public void createPost(String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("multipartFile imgs={}", imgs);
        List<Img> imgList = new ArrayList<>();
        List<Major> majorList = new ArrayList<>();

        List<ImgDto> imgDtoList = new ArrayList<>();

        if (imgs != null) {
            for (MultipartFile img : imgs) {
                ImgDto imgDto = fileUploadService.uploadImage(img, "post");
                imgDtoList.add(imgDto);
            }
        }

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.RequestDto requestDto = objectMapper.readValue(jsonString, PostDto.RequestDto.class);

        User user = loadUserByUserId(userDetails);
        dtoParser(imgList, imgDtoList, majorList, requestDto);

        // [유효성 검사] 제목, 내용, 기한, 지역, 모집 분야 입력 필수
        PostValidator.validateInputPost(requestDto);

        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .deadline(requestDto.getDeadline())
                .currentStatus(CurrentStatus.ONGOING)
                .region(requestDto.getRegion())
                .link(requestDto.getLink())
                .imgList(imgList)
                .majorList(majorList)
                .user(user)
                .build();
        postRepository.save(post);
    }

    // post 상세 조회
    @Transactional
    public PostDto.DetailDto getDetail(Long postId, UserDetailsImpl userDetails) {
        // postId에 해당하는 게시글
        Post post = loadPostByPostId(postId);
        
        /*
        1) 모집 글 쓴 유저와 로그인 유저가 같은 경우 : userStatus - starter
        2) 모집 지원한 유저와 로그인 유저가 같은 경우 : userStatus - applicant
        3) 모집 지원 후 수락된 유저와 로그인 유저가 같은 경우 : userStatus - member
        4) 로그인 유저(지원도, 팀원도 아님) : userStatus - user
        5) 로그인하지 않은 경우 : userStatus - anonymous
         */
        String userStatus;
        if (userDetails != null) {
            User user = userDetails.getUser();
            if (userApplyService.isStarter(post, user)) {   // 1)
                userStatus = UserStatus.USER_STATUS_TEAM_STARTER.getUserStatus();
            } else if (isMember(user, post) == 0) {   // 2)
                userStatus = UserStatus.USER_STATUS_APPLICANT.getUserStatus();
            } else if (isMember(user, post) == 1) { // 3
                userStatus = UserStatus.USER_STATUS_MEMBER.getUserStatus();
            } else {    // 4)
                userStatus = UserStatus.USER_STATUS_USER.getUserStatus();
            }
        } else {    // 5)
            userStatus = UserStatus.USER_STATUS_ANONYMOUS.getUserStatus();
        }

        // imgList
        List<String> imgUrl = imgRepository.findAllByPost(post)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

        // commentList
        List<Comment> findCommentByPost = commentRepository.findAllByPost(post);
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment comment : findCommentByPost) {
            commentList.add(new CommentResponseDto(
                    comment
                    ));
        }

        // majortList
        List<Major> findMajorByPost = majorRepository.findAllByPost(post);

        List<MajorDto.ResponseDto> majorList = new ArrayList<>();
        for (Major major : findMajorByPost) {
            majorList.add(new MajorDto.ResponseDto(major));
        }
        return new PostDto.DetailDto(userStatus, postId, post, imgUrl, commentList, majorList);
    }

    // post 수정
    @Transactional
    public void editPost(Long postId, String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("수정 내용={}", jsonString);

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.PutRequestDto putRequestDto = objectMapper.readValue(jsonString, PostDto.PutRequestDto.class);

        // postId에 해당하는 게시글
        Post post = loadPostByPostId(postId);

        // 로그인한 유저
        User user = loadUserByUserId(userDetails);

        // 본인 post만 수정 가능
        if (!post.getUser().equals(user)) {
            throw new CustomException(ErrorCode.POST_UPDATE_WRONG_ACCESS);
        }

        List<Img> imgList = post.getImgList();
        List<ImgDto> imgDtoList = new ArrayList<>();
        List<Img> removeImgList = new ArrayList<>();

        // 수정할 이미지 S3, 이미지 DB에서 삭제하기
        for (Img img : imgList) {
            for (ImgUrlDto imgUrlDto : putRequestDto.getImgUrl()) {
                if (img.getImgUrl().equals(imgUrlDto.getImgUrl())) {
                    s3UploadService.deleteFile(img.getImgName());
                    s3UploadService.deleteFile(img.getImgName().replace("post/", "post-resized/"));
                    imgRepository.deleteById(img.getId());
                    // removeImgList에 수정할 이미지 담기
                    removeImgList.add(img);
                }
            }
        }

        // / removeImgList에 담긴 수정 이미지 원래 Imglist에서 제거
        for (Img img : removeImgList) {
            imgList.remove(img);
        }

        // 추가할 이미지 S3에 저장
        if (imgs != null) {
            for (MultipartFile img : imgs) {
                if(!img.isEmpty()) {
                    ImgDto imgDto = fileUploadService.uploadImage(img, "post");
                    imgDtoList.add(imgDto);
                }
            }
        }
        putDtoParser(imgList, imgDtoList);
        post.updatePost(putRequestDto, imgList);
    }

    // post 삭제
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        // postId에 해당하는 게시글
        Post post = loadPostByPostId(postId);

        // 로그인한 유저
        User user = loadUserByUserId(userDetails);

        // 본인 post만 삭제 가능
        if (!post.getUser().equals(user)) {
            throw new CustomException(ErrorCode.POST_DELETE_WRONG_ACCESS);
        }

        // post 삭제시 s3에 저장된 이미지도 삭제
        List<Img> imgList = imgRepository.findAllByPost(post);
        for (Img img : imgList) {
            s3UploadService.deleteFile(img.getImgName());
            s3UploadService.deleteFile(img.getImgName().replace("post/", "post-resized/"));
        }

        // post 삭제시 평점 기록도 삭제
        userRateRepository.deleteAllByPostId(postId);

        postRepository.deleteById(postId);
    }

    // [예외 처리] postId에 해당하는 게시글 없을 경우
    private Post loadPostByPostId(Long PostId) {
        return postRepository.findById(PostId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );
    }

    // [예외 처리] 로그인한 유저 정보가 존배하지 않을 경우
    private User loadUserByUserId(UserDetailsImpl userDetails) {
        return  userRepository.findById(userDetails.getUser().getId()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
    }

    private void dtoParser(List<Img> imgList, List<ImgDto> imgDtoList, List<Major> majorList, PostDto.RequestDto requestDto) {
        for (ImgDto imgDto : imgDtoList) {
            Img img = Img.builder()
                    .imgName(imgDto.getImgName())
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
        }

        for (MajorDto.RequestDto majorRequestDto : requestDto.getMajorList()) {
            Major major = Major.builder()
                    .majorName(majorRequestDto.getMajorName())
                    .numOfPeopleSet(majorRequestDto.getNumOfPeopleSet())
                    .numOfPeopleApply(0)
                    .build();
            majorList.add(major);
        }
    }

    /*
    지원한 정보가 없을 경우 isAccepted = -1
    지원한 정보가 있을 경우 isAccepted = userApply.getIsAccepted
    userApply.getIsAccepted = 0 : 모집 지원 상태
    userApply.getIsAccepted = 1 : 모집 지원 후 수락된 상태
     */
    private int isMember(User user, Post post) {
        Optional<UserApply> userApplyOptional = userApplyRepository.findUserApplyByUserAndPost(user, post);
        int isAccepted;
        isAccepted = userApplyOptional.map(UserApply::getIsAccepted).orElse(-1);
        return isAccepted;
    }

    private void putDtoParser(List<Img> imgList, List<ImgDto> imgDtoList) {
        for (ImgDto imgDto : imgDtoList) {
            Img img = Img.builder()
                    .imgName(imgDto.getImgName())
                    .imgUrl(imgDto.getImgUrl())
                    .build();
            imgList.add(img);
        }
    }
}