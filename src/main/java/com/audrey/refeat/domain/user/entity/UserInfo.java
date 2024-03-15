package com.audrey.refeat.domain.user.entity;


import com.audrey.refeat.domain.user.dto.GoogleUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_as_cs")
    private String nickname;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column
    private String profileImage;

    @Column
    @Enumerated(EnumType.STRING)
    private Authority authority;


    @Builder
    public UserInfo(String email, String password, AuthProvider provider, String nickname, String profileImage, Long id){
        this.id = id;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authority = Authority.USER;
    }

    public static UserInfo fromSocialAuth(GoogleUser googleUser){
        return UserInfo.builder()
                .email(googleUser.getEmail())
                .provider(AuthProvider.GOOGLE)
                .nickname(googleUser.getName())
                .profileImage(googleUser.getPicture())
                .build();
    }

    public void updateProfileImage(String profileImage){
        this.profileImage = profileImage;
    }
    public void updateNickname(String nickname){
        this.nickname = nickname;
    }
    public void updatePassword(String password){
        this.password = password;
    }
}
