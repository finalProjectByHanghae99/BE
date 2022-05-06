package com.hanghae99.finalproject.user.model;

import com.hanghae99.finalproject.post.model.Post;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String majorName;

    private Integer numOfPeopleSet;

    private Integer numOfPeopleApply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public void updateApplyCount() {
        this.numOfPeopleApply+=1;
    }
}