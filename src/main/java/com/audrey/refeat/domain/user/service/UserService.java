package com.audrey.refeat.domain.user.service;

import com.audrey.refeat.common.exception.CustomException;
import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.common.s3.S3Component;
import com.audrey.refeat.common.s3.exception.FileUploadFailException;
import com.audrey.refeat.domain.user.component.JwtComponent;
import com.audrey.refeat.domain.user.dto.JwtUser;
import com.audrey.refeat.domain.user.dto.request.*;
import com.audrey.refeat.domain.user.dto.response.ProfileImageResponseDto;
import com.audrey.refeat.domain.user.dto.response.TokenResponseDto;
import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.domain.user.dto.response.UserDataResponseDto;
import com.audrey.refeat.domain.user.entity.AuthProvider;
import com.audrey.refeat.domain.user.entity.EmailVerification;
import com.audrey.refeat.domain.user.entity.UserInfo;
import com.audrey.refeat.domain.user.entity.dao.EmailVerificationRepository;
import com.audrey.refeat.domain.user.entity.dao.UserInfoRepository;
import com.audrey.refeat.domain.user.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserInfoRepository userInfoRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtComponent jwtComponent;
    private final JavaMailSender javaMailSender;
    private final Random random = new Random();
    private final S3Component s3Component;


    public RestResponseSimple createUser(RegisterRequestDto registerRequestDto) throws ExistedEmailException, EmailVerificationNotCommittedException {
        if (userInfoRepository.existsByEmail(registerRequestDto.email()))
            throw new ExistedEmailException();
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(registerRequestDto.email()).orElseThrow(EmailVerificationNotCommittedException::new);
        if (!emailVerification.isVerified())
            throw new EmailVerificationNotCommittedException();
        userInfoRepository.save(UserInfo
                .builder()
                .email(registerRequestDto.email())
                .nickname(registerRequestDto.email().split("@")[0])
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .profileImage(s3Component.getEndpoint("profile/default.jpeg"))
                .provider(AuthProvider.NATIVE)
                .build());
        return RestResponseSimple.success();
    }

    public RestResponse<TokenResponseDto> login(LoginRequestDto loginRequestDto) throws Exception {
        UserInfo userInfo = userInfoRepository.findByEmail(loginRequestDto.email()).orElseThrow(UserNotFoundException::new);
        if (!passwordEncoder.matches(loginRequestDto.password(), userInfo.getPassword()))
            throw new PasswordNotMatchedException();

        return RestResponse.ok(jwtComponent.createToken(userInfo));
    }

    public RestResponseSimple sendEmail(String email, boolean forPassword) throws ExistedEmailException, EmailSendFailedException {
        if (userInfoRepository.existsByEmail(email))
            throw new ExistedEmailException();
        SimpleMailMessage message = new SimpleMailMessage();
        Integer code = random.nextInt(888888) + 111111;
        String title = forPassword ? "ReFeat 비밀번호 변경" : "ReFeat 이메일 인증";
        message.setTo(email);
        message.setSubject(title);
        message.setText("인증번호는 " + code.toString() + "입니다.");
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendFailedException();
        }
        emailVerificationRepository.findByEmailAndChangePassword(email, forPassword).ifPresent(emailVerificationRepository::delete);
        emailVerificationRepository.save(EmailVerification.builder()
                .email(email)
                .code(code)
                .changePassword(forPassword)
                .build());
        return RestResponseSimple.success();
    }

    public RestResponseSimple checkEmail(String email, Integer code, boolean forPassword) throws Exception {
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndChangePassword(email, forPassword).orElseThrow(EmailVerificationNotCommittedException::new);
        if (emailVerification.isVerified())
            throw new EmailAlreadyVerifiedException();
        if (emailVerification.isExpired()) {
            emailVerificationRepository.delete(emailVerification);
            throw new EmailVerificationExpiredException();
        }
        if (emailVerification.getCode().equals(code)) {
            emailVerification.verify();
            emailVerificationRepository.save(emailVerification);
            return RestResponseSimple.success();
        } else {
            throw new EmailVerificationFailedException();
        }
    }

    public RestResponseSimple updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto) throws Exception {
        UserInfo userInfo = userInfoRepository.findByEmail(updatePasswordRequestDto.email()).orElseThrow(UserNotFoundException::new);
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndChangePassword(updatePasswordRequestDto.email(), true).orElseThrow(EmailVerificationNotCommittedException::new);
        if (!emailVerification.isVerified())
            throw new EmailVerificationNotCommittedException();
        if (emailVerification.isExpired()) {
            emailVerificationRepository.delete(emailVerification);
            throw new EmailVerificationExpiredException();
        }
        if (passwordEncoder.matches(updatePasswordRequestDto.password(), userInfo.getPassword())) {
            throw new CustomException(ErrorCode.SAME_PASSWORD);
        }
        userInfo.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.password()));
        userInfoRepository.save(userInfo);
        return RestResponseSimple.success();

    }

    public RestResponse<UserDataResponseDto> getUser() throws Exception {
        return RestResponse.ok(UserDataResponseDto.fromUserInfo(userInfoRepository.findById(JwtComponent.getUserInfo().id()).orElseThrow(UserNotFoundException::new)));
    }

    public RestResponse<TokenResponseDto> refresh(String refreshToken) throws Exception {
        ErrorCode errorCode = jwtComponent.validateToken(refreshToken, true);
        if (errorCode != ErrorCode.AUTH_SUCCESS)
            throw new CustomException(errorCode);
        UserInfo userInfo = jwtComponent.getUserInfo(refreshToken, true);
        return RestResponse.ok(jwtComponent.createToken(userInfo));
    }

    public RestResponse<ProfileImageResponseDto> updateProfileImage(MultipartFile profileImage) throws Exception {
        if (profileImage == null)
            throw new FileNotExistException();
        UserInfo userInfo = userInfoRepository.findById(JwtComponent.getUserInfo().id()).orElseThrow(UserNotFoundException::new);
        UUID uuid = UUID.randomUUID();
        String profilePath = "profile/" + uuid + ".jpeg";
        s3Component.uploadFileS3(profilePath, profileImage);
        s3Component.removeFileS3("profile/" + extractUUIDFromURL(userInfo.getProfileImage()) + ".jpeg");
        userInfo.updateProfileImage(s3Component.getEndpoint(profilePath));

        userInfoRepository.save(userInfo);
        return RestResponse.ok(new ProfileImageResponseDto(s3Component.getEndpoint(profilePath)));
    }

    public RestResponseSimple updateNickname(UpdateNicknameRequestDto updateNicknameRequestDto) throws Exception {
        UserInfo userInfo = userInfoRepository.findById(JwtComponent.getUserInfo().id()).orElseThrow(UserNotFoundException::new);
        if (Objects.equals(userInfo.getNickname(), updateNicknameRequestDto.nickname()))
            return RestResponseSimple.success();
        if (userInfoRepository.existsByNickname(updateNicknameRequestDto.nickname()))
            throw new ExistedNicknameException();
        userInfo.updateNickname(updateNicknameRequestDto.nickname());

        userInfoRepository.save(userInfo);
        return RestResponseSimple.success();
    }

    //////////// private method

    private static String extractUUIDFromURL(String url) {
        // Define a regex pattern to match UUID
        String regex = "/([a-f0-9]{8}(-[a-f0-9]{4}){3}-[a-f0-9]{12})\\.jpeg";
        Pattern pattern = Pattern.compile(regex);

        // Create a Matcher and find the UUID in the URL
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1); // Extract the matched UUID
        } else {
            return null; // UUID not found in the URL
        }
    }
}