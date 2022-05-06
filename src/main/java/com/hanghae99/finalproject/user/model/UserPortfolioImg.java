package com.hanghae99.finalproject.user.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Entity
public class UserPortfolioImg {
// 마이페이지에서 각 유저는 본인의 포폴 이미지를 업로드.

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    @JsonBackReference
    private User user;

    @Column
    private String portfolioImgName;

    @Column()
    private String portfolioImgUrl;


    public void updateUser(User user){
        this.user = user;
    }
}
