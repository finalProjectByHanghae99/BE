package com.hanghae99.finalprooject.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequestDto {

  //  private String accessToken;
  private String refreshToken;
  private Long userId;


}
