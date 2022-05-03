package com.hanghae99.finalproject.model;

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
}