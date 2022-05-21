package com.hanghae99.finalproject.sse.model;

public enum NotificationType {
        APPLY(" 모집글 신청이 도착했습니다.", "/"), ACCEPT(" 신청하신 모집글의 요청에 승인되었습니다.", "/"),
        REJECT(" 모집글 신청이 거절당하였습니다..", "/"), REPLY("에 댓글이 달렸습니다.", "/"),
        CHAT(" 채팅이 도착했습니다.", "/");

        private String content;
        private String url;

        NotificationType(String content, String url) {
            this.content = content;
            this.url = url;
        }

    public String makeContent(String title) {
        return "'" + title + "'" + content;
    }

    public String makeUrl(Long id) {
        return url + id;
    }

}
