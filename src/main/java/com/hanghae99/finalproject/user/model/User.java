package com.hanghae99.finalproject.user.model;

import com.hanghae99.finalproject.user.dto.MyPageDto;
import lombok.*;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    //닉네임
    @Column(nullable = false)
    private String nickname;

    //해당 유저 전공
    @Column(nullable = false)
    private String major;

    // 자기소개
    @Column
    private String intro;

    //프로필 이미지 -> DefaultImg
    @Column
    private String profileImg;

    //포트폴리오 링크
    @Column
    private String portfolioLink;

    //신청/모집 완료하여 마무리한 프로젝트의 수 .
    @Column
    private int projectCount;

    //평점 -> 좋아요를 받은 수
    @Column
    private int likeCount;

    @Column(columnDefinition = "boolean default false")
    private Boolean rateStatus;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserPortfolioImg>userPortfolioImgList;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserApply> userApplyList = new ArrayList<>();

    public void updateInfo( MyPageDto.RequestDto requestDto, List<UserPortfolioImg> userPortfolioImgs) {
        this.nickname = requestDto.getNickname();
        this.major = requestDto.getMajor();
        this.intro = requestDto.getIntro();
        this.profileImg = requestDto.getProfileImg();
        this.portfolioLink = requestDto.getPortfolioLink();
        this.userPortfolioImgList = userPortfolioImgs;
    }
    //평점을 받는다면 likeCount가 +1 -> 평가완료 !
    public void updateRateStatus(int point) {
        this.likeCount +=point;
        this.rateStatus = true;
    }
}