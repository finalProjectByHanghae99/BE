package com.hanghae99.finalproject.mail.controller;

import com.hanghae99.finalproject.exception.StatusResponseDto;
import com.hanghae99.finalproject.mail.service.MailService;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;

@Controller
@RequiredArgsConstructor
public class MailController {

    private final UserService userService;
    private final MailService mailService;

    // 이메일 인증 API
    @GetMapping("/user/email")
    public ResponseEntity<Object> emailAuthentication(@RequestParam String email,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws MessagingException {
        User user = userService.setEmailAuthCode(userDetails.getUser().getId());
        mailService.authMailSender(email, user);
        return new ResponseEntity<>(new StatusResponseDto("메일이 전송되었습니다", ""), HttpStatus.OK);
    }

    // 이메일 인증 코드 확인 API
    @GetMapping("/user/email/auth/{userId}")
    public String checkEmailAuthCode(@PathVariable Long userId, Model model,
                                     @RequestParam String code, @RequestParam String email) {
        String msg = mailService.emailAuthCodeCheck(code, userId, email);
        model.addAttribute("msg", msg);
        return "responsePage";  // 수정 필요
    }


}
