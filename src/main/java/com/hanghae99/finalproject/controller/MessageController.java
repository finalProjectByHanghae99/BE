package com.hanghae99.finalproject.controller;


import com.hanghae99.finalproject.dto.MessageDto;
import com.hanghae99.finalproject.dto.MessageListDto;
import com.hanghae99.finalproject.dto.RoomDto;
import com.hanghae99.finalproject.security.UserDetailsImpl;
import com.hanghae99.finalproject.service.MessageService;
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
    @MessageMapping("/message")
    public void message(@RequestBody MessageDto messageDto){
        messageService.sendMessage(messageDto);

    }

    @PostMapping("/api/roomcount")
    public void updateCount(@RequestBody RoomDto.UpdateCountDto updateCountDto){
        messageService.updateRoomMessageCount(updateCountDto);
    }

    //메시지 발행
    @PostMapping("/api/message")
    public MessageListDto showMessageList(@RequestBody RoomDto.findRoomDto roomDto,
                                          @PageableDefault(size = 200, sort = "createdAt") Pageable pageable,
                                          @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        return messageService.showMessageList(roomDto, pageable,userDetails);
    }




}

