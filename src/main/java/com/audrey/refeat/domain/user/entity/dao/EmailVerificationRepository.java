package com.audrey.refeat.domain.user.entity.dao;

import com.audrey.refeat.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.OptionalInt;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmail(String email);

    @Query("select e from EmailVerification e where e.email = ?1 and e.changePassword = ?2")
    Optional<EmailVerification> findByEmailAndChangePassword(String email, boolean changePassword);


}
