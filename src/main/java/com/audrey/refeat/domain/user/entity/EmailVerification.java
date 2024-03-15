package com.audrey.refeat.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String email;
    @Column
    private Integer code;
    @Column
    private LocalDateTime validUntil;
    @Column
    private boolean isVerified;
    @Column
    private boolean changePassword;

    @Builder
    public EmailVerification(String email, Integer code, Boolean changePassword){
        this.email = email;
        this.code = code;
        this.validUntil = LocalDateTime.now().plusMinutes(3);
        this.isVerified = false;
        this.changePassword = Objects.requireNonNullElse(changePassword, false);
    }

    public void verify(){
        this.isVerified = true;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(this.validUntil);
    }
}
