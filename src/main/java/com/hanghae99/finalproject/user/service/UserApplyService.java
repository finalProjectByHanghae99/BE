package com.hanghae99.finalproject.user.service;

import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.post.model.CurrentStatus;
import com.hanghae99.finalproject.post.model.Post;
import com.hanghae99.finalproject.post.repository.PostRepository;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.user.dto.UserApplyRequestDto;
import com.hanghae99.finalproject.user.model.Major;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.model.UserApply;
import com.hanghae99.finalproject.user.repository.MajorRepository;
import com.hanghae99.finalproject.user.repository.UserApplyRepository;
import com.hanghae99.finalproject.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplyService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MajorRepository majorRepository;
    private final UserApplyRepository userApplyRepository;

    // 모집 지원
    @Transactional
    public UserApply apply(Long postId, UserApplyRequestDto userApplyRequestDto, UserDetailsImpl userDetails) {

        // [예외 처리] 조회하는 게시물이 존재하지 않을 경우
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        // [예외 처리] 요청하는 유저 정보가 존재하지 않을 경우
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // [예외 처리] 본인이 모집하는 프로젝트에 신청할 경우
        if (post.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new CustomException(ErrorCode.APPLY_WRONG_ERROR);
        }

        // [예외 처리] 정원 마감, 모집 완료인 프로젝트에 신청할 경우
        if (!post.getCurrentStatus().equals(CurrentStatus.ONGOING)) {
            throw new CustomException(ErrorCode.ALREADY_STARTED_ERROR);
        }

        // [예외 처리] 신청했던 프로젝트에 다시 신청할 경우
        if (userApplyRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            throw new CustomException(ErrorCode.ALREADY_APPLY_POST_ERROR);
        }

        // [유효성 검사] 선택한 지원 분야 없을 경우 에러 메시지("지원할 분야를 선택해주세요")
        String applyMajor = userApplyRequestDto.getApplyMajor();
        if (!StringUtils.hasText(applyMajor)) {
            throw new CustomException(ErrorCode.APPLY_MAJOR_WRONG_INPUT);
        }

        // [유효성 검사] 에러 메시지 글자수 (띄어쓰기 제외 20자 이내)
        if (applyMajor.trim().length() > 20) {
            throw new CustomException(ErrorCode.APPLY_MESSAGE_INPUT_LENGTH_ERROR);
        }

        List<Major> findMajorByPost = majorRepository.findAllByPost(post);
        List<String> majorList = new ArrayList<>();
        for (Major major : findMajorByPost) {
            majorList.add(major.getMajorName());
        }

        // [유효성 검사] 선택한 지원 분야가 모집하는 지원 분야와 일치하지 않을 경우
        if (!majorList.contains(applyMajor)) {
            throw new CustomException(ErrorCode.APPLY_MAJOR_NOT_EXIST);
        }

        // [유효성 검사] 선택한 지원 분야의 정원이 이미 다 찼을 경우
        Major findMajor = majorRepository.findByPostAndMajorName(post, applyMajor);
        if (findMajor.getNumOfPeopleApply() == findMajor.getNumOfPeopleSet()) {
            throw new CustomException(ErrorCode.APPLY_PEOPLE_SET_CLOSED);
        }

        // [Default] 지원 메시지 비었을 경우 Default 메시지 설정
        String message = userApplyRequestDto.getMessage();
        if (!StringUtils.hasText(message)) {
            message = "잘 부탁드립니다!";
        }

        UserApply userApply = UserApply.builder()
                .post(post)
                .user(user)
                .message(message)
                .applyMajor(applyMajor)
                .build();

        return userApplyRepository.save(userApply);
    }

    // 모집 지원 취소
    @Transactional
    public void cancelApply(Long postId, UserDetailsImpl userDetails) {

        // [예외 처리] 조회하는 게시물이 존재하지 않을 경우
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        // [예외 처리] 요청하는 유저 정보가 존재하지 않을 경우
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // [예외 처리] 모집 지원 정보가 없을 경우
        UserApply userApply = userApplyRepository.findByUserAndPost(user, post).orElseThrow(
                () -> new CustomException(ErrorCode.APPLY_NOT_FOUND)
        );

        userApply.cancelApply();
        userApplyRepository.delete(userApply);
    }

    /*
    모집 마감
    CurrentStatus 변경(ONGOING → RECRUITING_COMPLETE)
    프로젝트 참가 유저 ProjectCount += 1
     */
    @Transactional
    public void overApply(Long postId, UserDetailsImpl userDetails) {

        CurrentStatus newStatus = CurrentStatus.RECRUITING_COMPLETE;

        // [예외 처리] 조회하는 게시물이 존재하지 않을 경우
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND)
        );

        // [예외 처리] 요청하는 유저 정보가 존재하지 않을 경우
        User user = userRepository.findByNickname(userDetails.getUser().getNickname()).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        // [예외 처리] 모집 글 쓴 유저가 아닌 유저가 모집 마감하려는 경우
        if (!isStarter(post, user)) {
            throw new CustomException(ErrorCode.APPLY_OVER_NO_AUTHORITY);
        }

        // CurrentStatus 변경하기
        CurrentStatus currentStatus = post.getCurrentStatus();

        if (newStatus.equals(currentStatus)) {
            throw new CustomException(ErrorCode.NO_DIFFERENCE_STATUS);
        } else {
            post.updateStatus(newStatus);
        }

        /*
        모집 마감 시  유저의 projectCount 올리기
        1) 모집한 유저의 projectCount 올리기
        2) 참여한 유저의 projectCount 올리기
         */
        // 해당 프로젝트에 지원한 user list에 담기
        List<UserApply> userApplyList = userApplyRepository.findAllByPost(post);

        int startNewProjectCount = post.getUser().getProjectCount() + 1;

        if (post.getCurrentStatus() == CurrentStatus.RECRUITING_COMPLETE) {
            // 1)
            user.updateProjectCount(startNewProjectCount);
            // 2)
            for (UserApply userApply : userApplyList) {
                if (userApply.getIsAccepted() == 1) {   // 지원 유저 중 프로젝트 수락된 유저만 해당
                    Long memberId = userApply.getUser().getId();
                    // 수락 유저의 userId로 현재 존재하는 유저인지 확인
                    Optional<User> member = userRepository.findById(memberId);
                    int memberNewProjectCount = member.get().getProjectCount() + 1;

                            // 존재한다면 projectCount += 1
                    member.ifPresent(value -> value.updateProjectCount(memberNewProjectCount));
                }
            }
        }
    }

    // 모집 글 쓴 유저와 로그인한 유저 정보 일치 여부 확인
    public boolean isStarter(Post post, User user) {
        Long starterId = post.getUser().getId();    // 모집 글 쓴 유저
        Long loginUserId = user.getId();    // 현재 로그인 한 유저
        return starterId.equals(loginUserId);
    }
}