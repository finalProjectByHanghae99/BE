package com.hanghae99.finalproject.post.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanghae99.finalproject.comment.dto.CommentDto;
import com.hanghae99.finalproject.comment.model.Comment;
import com.hanghae99.finalproject.comment.repository.CommentRepository;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.PrivateException;
import com.hanghae99.finalproject.img.Img;
import com.hanghae99.finalproject.img.ImgRepository;
import com.hanghae99.finalproject.post.dto.PostDto;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.img.AwsS3UploadService;
import com.hanghae99.finalproject.img.FileUploadService;
import com.hanghae99.finalproject.img.ImgDto;
import com.hanghae99.finalproject.img.ImgUrlDto;
import com.hanghae99.finalproject.user.dto.MajorDto;
import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import com.hanghae99.finalproject.user.repository.MajorRepository;
import com.hanghae99.finalproject.user.repository.UserApplyRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final FileUploadService fileUploadService;
    private final AwsS3UploadService s3UploadService;

    // post 전체 조회
    public Map<String, List<PostDto.ResponseDto>> home() {
        Map<String, List<PostDto.ResponseDto>> mapList = new HashMap<>();
        List<PostDto.ResponseDto> list = new ArrayList<>();
        for (Post post : postRepository.findAllByOrderByCreatedAtDesc()) {

            List<String> imgUrlList = imgRepository.findAllByPost(post)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

            String imgUrl;
            if (imgUrlList.isEmpty()) {
                imgUrl = "https://hyemco-butket.s3.ap-northeast-2.amazonaws.com/postDefaultImg.PNG";
            } else {
                imgUrl = imgUrlList.get(0);
            }

            List<Major> findMajorByPost = majorRepository.findAllByPost(post);
            List<MajorDto.ResponseDto> majorList = new ArrayList<>();
            for (Major major : findMajorByPost) {
                majorList.add(new MajorDto.ResponseDto(major));
            }
            PostDto.ResponseDto home = new PostDto.ResponseDto(post, imgUrl, majorList);
            list.add(home);
        }
        mapList.put("date", list);
        return mapList;
    }

    // post 등록
    @Transactional
    public void createPost(String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("multipartFile imgs={}", imgs);
        List<Img> imgList = new ArrayList<>();
        List<Major> majorList = new ArrayList<>();

        List<ImgDto> imgDtoList = new ArrayList<>();

        if (imgs != null) {
            for (MultipartFile img : imgs) {
                ImgDto imgDto = fileUploadService.uploadImage(img);
                imgDtoList.add(imgDto);
            }
        }

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.RequestDto requestDto = objectMapper.readValue(jsonString, PostDto.RequestDto.class);

        User user = userDetails.getUser();
        dtoParser(imgList, imgDtoList, majorList, requestDto);
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

    // post 상세 조회
    @Transactional
    public PostDto.DetailDto getDetail(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

//        UserApply userApply = userApplyRepository.findByUserAndPost(user, post).orElseThrow(
//                () -> new PrivateException(ErrorCode.APPLY_NOT_FOUND)
//        );

//        UserApply userApply = userApplyRepository.findByUserAndPost(user, post).ifPresent();

        // imgList
        List<String> imgUrl = imgRepository.findAllByPost(post)
                .stream()
                .map(Img::getImgUrl)
                .collect(Collectors.toList());

        // commentList
        List<Comment> findCommentByPost = commentRepository.findAllByPost(post);
        List<CommentDto.ResponseDto> commentList = new ArrayList<>();
        for (Comment comment : findCommentByPost) {
            commentList.add(new CommentDto.ResponseDto(
                    comment,
                    comment.getUser().getId(),
                    comment.getUser().getNickname(),
                    comment.getUser().getProfileImg()
                    ));
        }

        // majortList
        List<Major> findMajorByPost = majorRepository.findAllByPost(post);

        List<MajorDto.ResponseDto> majorList = new ArrayList<>();
        for (Major major : findMajorByPost) {
            majorList.add(new MajorDto.ResponseDto(major));
        }
        return new PostDto.DetailDto(postId, post, imgUrl, commentList, majorList);
    }

    // post 수정
    @Transactional
    public void editPost(Long postId, String jsonString, List<MultipartFile> imgs, UserDetailsImpl userDetails) throws IOException {
        log.info("수정 내용={}", jsonString);

        // 자료형이 String인 jsonString을 Dto로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        PostDto.PutRequestDto putRequestDto = objectMapper.readValue(jsonString, PostDto.PutRequestDto.class);

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 post만 수정 가능
        if (!post.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.POST_UPDATE_WRONG_ACCESS);
        }

        List<Img> imgList = post.getImgList();
        List<ImgDto> imgDtoList = new ArrayList<>();
        List<Img> removeImgList = new ArrayList<>();
        // 수정할 이미지 S3, 이미지 DB에서 삭제하기
        for (Img img : imgList) {
            for (ImgUrlDto imgUrlDto : putRequestDto.getImgUrl()) {
                if (img.getImgUrl().equals(imgUrlDto.getImgUrl())) {
                    s3UploadService.deleteFile(img.getImgName());
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
                    ImgDto imgDto = fileUploadService.uploadImage(img);
                    imgDtoList.add(imgDto);
                }
            }
        }
        putDtoParser(imgList, imgDtoList);
        post.updatePost(putRequestDto, imgList);
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



    // post 삭제
    @Transactional
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new PrivateException(ErrorCode.POST_NOT_FOUND)
        );

        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new PrivateException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // 본인 post만 삭제 가능
        if (!post.getUser().equals(user)) {
            throw new PrivateException(ErrorCode.POST_DELETE_WRONG_ACCESS);
        }

        postRepository.deleteById(postId);
    }
}