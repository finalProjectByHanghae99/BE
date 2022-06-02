package com.hanghae99.finalproject.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.omg.CORBA.OBJECT_NOT_EXIST;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    OK(HttpStatus.OK,  "200", "true"),


    //문자열 체크
    NOT_VALIDCONTENT(HttpStatus.BAD_REQUEST,"400","유효하지 않는 내용입니다."),
    NOT_VALIDURL(HttpStatus.BAD_REQUEST,"400","요효하지 않는 URL 입니다."),

    // 회원가입
    SIGNUP_MEMBERID_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "아이디 형식을 맞춰주세요"),
    SIGNUP_PASSWORD_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "비밀번호 형식을 맞춰주세요"),
    SIGNUP_PWCHECK_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "비밀번호가 일치하지 않습니다"),
    SIGNUP_NICKNAME_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "닉네임 형식을 맞춰주세요"),
    SIGNUP_MEMBERID_DUPLICATE_CHECK(HttpStatus.BAD_REQUEST, "400", "아이디 중복확인을 해주세요"),
    SIGNUP_NICKNAME_DUPLICATE_CHECK(HttpStatus.BAD_REQUEST, "400", "닉네임 중복확인을 해주세요"),
    SIGNUP_MAJOR_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "분야를 선택해주세요"),
    SIGNUP_USERID_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "userId가 존재하지 않습니다"),

    SIGNUP_MEMBERID_DUPLICATE(HttpStatus.BAD_REQUEST, "400", "해당 아이디가 이미 존재합니다"),
    SIGNUP_MEMBERID_CORRECT(HttpStatus.OK, "200", "사용할 수 있는 아이디입니다"),
    SIGNUP_NICKNAME_DUPLICATE(HttpStatus.BAD_REQUEST, "400", "해당 닉네임이 이미 존재합니다"),
    SIGNUP_NICKNAME_CORRECT(HttpStatus.OK, "200", "사용할 수 있는 닉네임입니다"),

    // Token
    JWT_TOKEN_WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "401", "잘못된 JWT 서명입니다"),
    JWT_TOKEN_NOT_SUPPORTED(HttpStatus.UNAUTHORIZED, "401", "지원되지 않는 JWT 토큰입니다."),
    JWT_TOKEN_WRONG_FORM(HttpStatus.UNAUTHORIZED, "401", "JWT 토큰이 잘못되었습니다."),

    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "401", "로그인이 만료되었습니다. 재로그인 하세요."),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "Refresh Token이 존재하지 않습니다. 로그인 해주세요"),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED, "401", "Refresh Token이 일치하지 않습니다"),
    REFRESH_TOKEN_REISSUE_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "userId, accessToken, refreshToken을 입력해주세요"),

    // 로그인
    LOGIN_NOT_FOUNT_MEMBERID(HttpStatus.NOT_FOUND, "404", "해당 아이디를 찾을 수 없습니다"),
    LOGIN_MEMBERID_EMPTY(HttpStatus.BAD_REQUEST, "400", "아이디를 입력해주세요"),
    LOGIN_PASSWORD_EMPTY(HttpStatus.BAD_REQUEST, "400", "비밀번호를 입력해주세요"),
    LOGIN_PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, "400", "비밀번호가 틀렸습니다. 다시 입력해주세요"),

    // 회원 탈퇴
    NOT_MATCH_USER_INFO(HttpStatus.BAD_REQUEST, "400", "유저 정보가 일치하지 않습니다"),

    //기타
    NOT_FOUND_AUTHORIZATION_IN_SECURITY_CONTEXT(HttpStatus.INTERNAL_SERVER_ERROR, "998", "Security Context에 인증 정보가 없습니다."),
    NOT_FOUND_USER_INFO(HttpStatus.NOT_FOUND, "404", "해당 유저가 존재하지 않습니다"),

    // 이미지
    WRONG_INPUT_IMAGE(HttpStatus.BAD_REQUEST, "400", "이미지는 반드시 있어야 합니다"),
    IMAGE_UPLOAD_ERROR(HttpStatus.BAD_REQUEST, "400", "이미지 업로드에 실패했습니다"),
    WRONG_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "400", "지원하지 않는 파일 형식입니다"),

    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 게시물을 찾을 수 없습니다"),
    POST_UPDATE_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "400", "본인의 게시물만 수정할 수 있습니다"),
    POST_DELETE_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "400", "본인의 게시물만 삭제할 수 있습니다"),
    POST_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "비어있는 항목을 채워주세요"),
    POST_MAJOR_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "모집 분야를 선택해주세요"),
    POST_TITLE_INPUT_LENGTH_ERROR(HttpStatus.BAD_REQUEST, "400", "제목을 공백 포함 20자 이내로 작성해주세요"),
    POST_CONTENT_INPUT_LENGTH_ERROR(HttpStatus.BAD_REQUEST, "400", "내용을 공백 포함 250자 이내로 작성해주세요"),

    // comment
    COMMENT_WRONG_INPUT(HttpStatus.BAD_REQUEST, "400", "댓글을 입력해주세요"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404", "해당 댓글을 찾을 수 없습니다"),
    COMMENT_UPDATE_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "400", "본인의 댓글만 수정할 수 있습니다"),
    COMMENT_DELETE_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "400", "본인의 댓글만 삭제할 수 있습니다"),
    USER_UPDATE_WRONG_ACCESS(HttpStatus.BAD_REQUEST, "400", "본인이 아니면 수정할 수 없습니다"),

    // apply
    APPLY_WRONG_ERROR(HttpStatus.BAD_REQUEST, "400", "본인의 프로젝트에 지원 신청 할 수 없습니다"),
    ALREADY_STARTED_ERROR(HttpStatus.BAD_REQUEST, "400", "모집중인 프로젝트가 아닙니다"),
    ALREADY_APPLY_POST_ERROR(HttpStatus.BAD_REQUEST,"400", "이미 지원한 프로젝트입니다"),
    APPLY_MAJOR_WRONG_INPUT(HttpStatus.BAD_REQUEST,"400", "지원할 분야를 선택해주세요"),
    APPLY_MAJOR_NOT_EXIST(HttpStatus.NOT_FOUND, "404","해당 분야는 모집하지 않습니다"),
    APPLY_PEOPLE_SET_CLOSED(HttpStatus.BAD_REQUEST, "400", "해당 분야의 정원이 다 찼습니다"),
    APPLY_NOT_FOUND(HttpStatus.BAD_REQUEST,"404", "해당 지원 정보를 찾을 수 없습니다"),
    APPLY_OVER_NO_AUTHORITY(HttpStatus.FORBIDDEN,"403", "권한이 없습니다"),
    APPLY_MESSAGE_INPUT_LENGTH_ERROR(HttpStatus.BAD_REQUEST, "400", "지원 메시지는 20자 이내로 작성해주세요"),
    EXCEED_APPLY_USER_NUMBER(HttpStatus.INTERNAL_SERVER_ERROR,"500","전공 모집인원이 초과되었습니다."),

    NO_DIFFERENCE_STATUS(HttpStatus.FORBIDDEN,"403", "Status 변경 사항이 없습니다"),

    // mail
    EMAIL_WRONG_PATTERN(HttpStatus.BAD_REQUEST, "400", "이메일 형식을 맞춰주세요"),

    //Room

    ALREADY_EXISTS_CHAT_ROOM(HttpStatus.BAD_REQUEST,"400","채팅방이 이미 존재합니다."),
    NOT_EXIST_ROOM(HttpStatus.NOT_FOUND,"404","채팅방이 존재하지 않습니다."),

    //sse
    NOT_EXIST_NOTIFICATION(HttpStatus.NOT_FOUND,"404","존재하지 않는 알림입니다.");



    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
}