package com.hanghae99.finalproject.user.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.hanghae99.finalproject.user.dto.MyPageDto;
import lombok.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity
@DynamicUpdate // null 값인 field 를 DB에서 설정된 default을 줌
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

    @Column
    @ColumnDefault("false")
    private Boolean rateStatus;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<UserPortfolioImg>userPortfolioImgList;

    @Builder.Default
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserApply> userApplyList = new ArrayList<>();

    public void updateInfo( MyPageDto.RequestDto requestDto, List<UserPortfolioImg> userPortfolioImgList) {
        this.nickname = requestDto.getNickname();
        this.major = requestDto.getMajor();
        this.intro = requestDto.getIntro();
        this.profileImg = requestDto.getProfileImg();
        this.portfolioLink = requestDto.getPortfolioLink();
        this.userPortfolioImgList = userPortfolioImgList;
        for(UserPortfolioImg userPortfolioImg : userPortfolioImgList){
            userPortfolioImg.updateUser(this);
        }
    }

    //평점을 받는다면 likeCount가 +1 -> 평가완료 !
    public void updateRateStatus(int point) {
        this.likeCount +=point;
        this.rateStatus = true;
    }

    // 모집 마감시 ProjectCount 변경(projectCount += 1)
    public void updateProjectCount(int newProjectCount) {
        this.projectCount = newProjectCount;
    }
}