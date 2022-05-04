package com.hanghae99.finalproject.config;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import org.apache.commons.text.StringEscapeUtils;

public class HtmlCharacterEscapes  extends CharacterEscapes {
    private final int[] asciiEscapes;

    // xss 란? Cross-site Scripting의 약어로, 사이트 간 스크립팅을 의미
    // 웹사이트 관리자가 아닌 사람이 웹 페이지에 악성 자바스크립트를 삽입 할 수 있음
    // xss의 주 목적은 웹사이트의 변조보다는, 사용자의 세션 탈취
    // 해킹 시나리오 : 악성스크립트를 담은 게시물 등록 - 이용 유저들이 열람 - 열람자의 쿠키 값을 가로챔
    // 가로 챈 쿠키 값을 웹 프록시 툴을 이용하여 재전송 - 열람자의 정보로 로그인
    // 만약, 열람자가 관리자라면, 관리자로 로그인 가능

    public HtmlCharacterEscapes() {
        // xss 방지 처리할 특수 문자 지정
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\"'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['('] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes[')'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['#'] = CharacterEscapes.ESCAPE_CUSTOM;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
    }

    @Override
    public int[] getEscapeCodesForAscii() {

        return asciiEscapes;
    }



    @Override
    public SerializableString getEscapeSequence(int ch) {
        SerializedString serializedString = null;
        char charAt = (char) ch;
        //emoji jackson parse 오류에 따른 예외 처리
        if (Character.isHighSurrogate(charAt) || Character.isLowSurrogate(charAt)) {
            StringBuilder sb = new StringBuilder();
            sb.append("\\u");
            sb.append(String.format("%04x",ch));
            serializedString = new SerializedString(sb.toString());
        } else {
            serializedString =
                    new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString(charAt)));
        } return serializedString;




    }


}
