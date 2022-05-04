package com.hanghae99.finalproject.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageListDto {

    private List<MessageDto> message;

}
