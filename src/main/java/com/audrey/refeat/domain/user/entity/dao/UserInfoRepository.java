package com.audrey.refeat.domain.user.entity.dao;

import com.audrey.refeat.domain.user.entity.AuthProvider;
import com.audrey.refeat.domain.user.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    @Query("select u from UserInfo u where u.nickname = ?1")
    Optional<UserInfo> findByNickname(String nickname);

    boolean existsByEmail(String email);

    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByEmailAndProvider(String email, AuthProvider provider);

    @Query("select (count(u) > 0) from UserInfo u where u.nickname = ?1")
    boolean existsByNickname(String nickname);

    @Override
    Optional<UserInfo> findById(Long aLong);

    @Query("select u from UserInfo u, Project p left join p.user where p.id = ?1")
    List<UserInfo> findByProjectId(Long projectId);
}
