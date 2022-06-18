package com.hanghae99.finalproject.chatRoom.controller;


import com.hanghae99.finalproject.chatRoom.dto.MessageDto;
import com.hanghae99.finalproject.chatRoom.dto.MessageListDto;
import com.hanghae99.finalproject.chatRoom.dto.RoomDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.chatRoom.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class MessageController {
    private final MessageService messageService;

    // /pub/message -> StompConfig에서 설정한 prefix 값과 결합
    // pub[메시지발행 ] -> topic 생성 -> sub 구독
    @MessageMapping("/message")
    public void message(@RequestBody MessageDto messageDto){
        messageService.sendMessage(messageDto);
    }

    //발행된 메시지 조회
    @PostMapping("/api/message")
    public MessageListDto showMessageList(@RequestBody RoomDto.findRoomDto roomDto,
                                          @PageableDefault(size = 200, sort = "createdAt") Pageable pageable,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return messageService.showMessageList(roomDto, pageable,userDetails);
    }




}

