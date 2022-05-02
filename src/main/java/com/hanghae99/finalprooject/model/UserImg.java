package com.hanghae99.finalprooject.model;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;


@Builder
@Getter
@NoArgsConstructor
@Entity
public class UserImg {


    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

}
