package com.hanghae99.finalproject.mail.service;

import com.hanghae99.finalproject.exception.CustomException;
import com.hanghae99.finalproject.exception.ErrorCode;
import com.hanghae99.finalproject.mail.dto.MailDto;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import com.hanghae99.finalproject.validator.MailValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final UserRepository userRepository;

    public void setMail(String subject, String body, String email) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setTo(email); // 메일 수신자
        mimeMessageHelper.setSubject(subject);  // 메일 제목
        mimeMessageHelper.setText(body, true);  // 메일 내용
        sendMail(mimeMessageHelper.getMimeMessage());
    }

    private void sendMail(MimeMessage message) {
        javaMailSender.send(message);
    }

    // 이메일 인증 메일 발송
    public void authMailSender(String email, User user) throws MessagingException {

        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("userId", user.getId());
        context.setVariable("profileImg", user.getProfileImg());
        context.setVariable("nickname", user.getNickname());
        context.setVariable("email", email);
        context.setVariable("code", user.getEmailAuthCode());

        String subject = "[모험:모두의 경험] " + user.getNickname() + "님! 이메일 인증을 완료해주세요.";
        String body = templateEngine.process("authenticationEmail", context);
        setMail(subject, body, email);
    }

    // 이메일 인증 확인
    @Transactional
    public String emailAuthCodeCheck(String code, Long userId, String email) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );

        if (user.getEmailAuthCode().equals(code)) {
            user.verifiedEmail(email);
            return "이메일 인증이 완료되었습니다";
        } else {
            return "유효한 인증 코드가 아닙니다";
        }
    }

    // 지원 알림 메일 발송
    public void applicantMailBuilder(MailDto mailDto) throws MessagingException {

        String email = mailDto.getToEmail();
        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("toNickname", mailDto.getToNickname());
        context.setVariable("fromNickname", mailDto.getFromNickname());
        context.setVariable("fromProfileImg", mailDto.getFromProfileImg());
        context.setVariable("postId", mailDto.getPostId());

        String subject = "[모험:모두의 경험] " + mailDto.getToNickname() + "님! 프로젝트 신청 알림이 도착했습니다.";
        String body = templateEngine.process("applicantEmail", context);
        setMail(subject, body, email);
    }

    // 수락 알림 메일 발송
    public void acceptTeamMailBuilder(MailDto mailDto) throws MessagingException {

        String email = mailDto.getToEmail();
        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("toNickname", mailDto.getToNickname());
        context.setVariable("fromNickname", mailDto.getFromNickname());
        context.setVariable("fromProfileImg", mailDto.getFromProfileImg());
        context.setVariable("postId", mailDto.getPostId());

        String subject = "[모험:모두의 경험] " + mailDto.getToNickname() + "님! 프로젝트 매칭 알림이 도착했습니다.";
        String body = templateEngine.process("acceptTeamEmail", context);
        setMail(subject, body, email);
    }

    // 거절 알림 메일 발송
    public void rejectTeamMailBuilder(MailDto mailDto) throws MessagingException {

        String email = mailDto.getToEmail();
        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("toNickname", mailDto.getToNickname());
        context.setVariable("toProfileImg", mailDto.getToProfileImg());
        context.setVariable("fromNickname", mailDto.getFromNickname());
        context.setVariable("postId", mailDto.getPostId());

        String subject = "[모험:모두의 경험] " + mailDto.getToNickname() + "님! 프로젝트 매칭 실패 알림이 도착했습니다.";
        String body = templateEngine.process("rejectTeamEmail", context);
        setMail(subject, body, email);
    }

    // 강퇴 알림 메일 발송
    public void forcedRejectTeamEmailBuilder(MailDto mailDto) throws MessagingException {

        String email = mailDto.getToEmail();
        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("toNickname", mailDto.getToNickname());
        context.setVariable("toProfileImg", mailDto.getToProfileImg());
        context.setVariable("fromNickname", mailDto.getFromNickname());
        context.setVariable("postId", mailDto.getPostId());

        String subject = "[모험:모두의 경험] " + mailDto.getToNickname() + "님! 프로젝트 하차 알림이 도착했습니다.";
        String body = templateEngine.process("forcedRejectTeamEmail", context);
        setMail(subject, body, email);
    }

    // 신규 채팅방 생성 알림 메일 발송
    public void chatOnEmailBuilder(MailDto mailDto) throws MessagingException {

        String email = mailDto.getToEmail();
        // [유효성 검사] 이메일 형식 확인
        MailValidator.validateEmail(email);

        Context context = new Context();
        context.setVariable("toNickname", mailDto.getToNickname());
        context.setVariable("fromNickname", mailDto.getFromNickname());
        context.setVariable("fromProfileImg",mailDto.getFromProfileImg());
        context.setVariable("postId", mailDto.getPostId());
        context.setVariable("postTitle", mailDto.getPostTitle());

        String subject = "[모험:모두의 경험] " + mailDto.getToNickname() + "님, " + mailDto.getFromNickname() + "님과 대화를 시작해보세요!";
        String body = templateEngine.process("chatOnEmil", context);
        setMail(subject, body, email);
    }
}