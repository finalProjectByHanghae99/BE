package com.hanghae99.finalprooject.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity
public class RefreshToken {

    @Id
    private String refreshKey;

    private String refreshValue;

    public RefreshToken(String refreshKey, String refreshValue) {
        this.refreshKey = refreshKey;
        this.refreshValue = refreshValue;
    }

    public RefreshToken updateValue(String token) {
        this.refreshValue = token;
        return this;
    }
}