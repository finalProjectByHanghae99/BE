package com.hanghae99.finalprooject.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Room {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String roomName; // UUID로 받을 예정

    @Column(nullable = false)
    private Long roomPostId; // 어떤 게시글에서 생성된 방인지 확인 ,

}
