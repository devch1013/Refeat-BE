package com.audrey.refeat.domain.user.controller;

import com.audrey.refeat.common.response.RestResponse;
import com.audrey.refeat.common.response.RestResponseSimple;
import com.audrey.refeat.domain.user.dto.request.*;
import com.audrey.refeat.domain.user.dto.response.ProfileImageResponseDto;
import com.audrey.refeat.domain.user.dto.response.TokenResponseDto;
import com.audrey.refeat.domain.user.dto.response.UserDataResponseDto;
import com.audrey.refeat.domain.user.exception.EmailVerificationNotCommittedException;
import com.audrey.refeat.domain.user.exception.ExistedEmailException;
import com.audrey.refeat.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "User API")
@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;


    @Operation(summary = "회원가입",
            description = "소셜로그인이 아닌 이메일 회원가입입니다. 이메일 인증후 진행해주시면 됩니다." +
                    "<br>이메일 인증 API는 아래에 있습니다." +
                    "<br>이메일 인증 실패시 에러코드가 반환됩니다." +
                    "<br>(409, \"UE003\", \"이미 가입된 이메일입니다.\")" +
                    "<br>(409, \"UE005\", \"이메일 인증이 되지 않은 이메일입니다.\")"
    )
    @PostMapping("/register")
    public ResponseEntity<RestResponseSimple> register(@RequestBody RegisterRequestDto registerRequestDto) throws ExistedEmailException, EmailVerificationNotCommittedException {
        return ResponseEntity.ok(userService.createUser(registerRequestDto));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 주시면 access key와 refresh key가 반환됩니다." +
            "<br>(401, \"UE001\", \"사용자를 찾을 수 없습니다.\")" +
            "<br>(401, \"UE002\", \"비밀번호가 일치하지 않습니다.\")")
    @PostMapping("/login")
    public ResponseEntity<RestResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) throws Exception {
        return ResponseEntity.ok(userService.login(loginRequestDto));
    }

    @Operation(summary = "이메일 인증번호 발송", description = "body에 email을 담아 주시면 인증번호를 담은 이메일이 발송됩니다. <br>" +
            "이메일 발송 후 이메일 인증번호 확인 API 사용해주시면 됩니다." +
            "<br>인증번호 입력 제한시간은 3분 입니다." +
            "<br>비밀번호 변경을 위한 인증번호 발송은 parameter forPassword=true를 담아서 사용하시면 됩니다." +
            "<br>(409, \"UE003\", \"이미 가입된 이메일입니다.\")" +
            "<br>(500, \"EE001\", \"이메일 발송에 실패했습니다.\")")
    @PostMapping("/email")
    public ResponseEntity<RestResponseSimple> emailAuthentication(@RequestBody EmailAuthenticationDto emailAuthenticationDto,
                                                                  @RequestParam(required = false, defaultValue = "false") boolean forPassword) throws Exception {
        return ResponseEntity.ok(userService.sendEmail(emailAuthenticationDto.email(), forPassword));
    }

    @Operation(summary = "이메일 인증번호 확인", description = "이메일과 사용자가 입력한 인증 코드를 보내주시면 체크 후 결과가 반환됩니다. " +
            "<br>인증 성공시 해당 정보가 DB에 바로 반영되기 때문에 바로 회원가입을 진행해주시면 됩니다." +
            "<br>비밀번호 변경을 위한 인증은 parameter forPassword=true를 담아서 사용하시면 됩니다." +
            "<br>(409, \"UE007\", \"이미 인증된 이메일입니다.\")" +
            "<br>(409, \"UE006\", \"인증번호가 일치하지 않습니다.\")" +
            "<br>(409, \"UE005\", \"이메일 인증이 되지 않은 이메일입니다.\") - 인증번호가 발송되지않은 이메일" +
            "<br>(409, \"UE009\", \"인증번호가 만료되었습니다.\")")
    @PostMapping("/email/check")
    public ResponseEntity<RestResponseSimple> emailCheck(@RequestBody EmailCheckDto emailCheckDto,
                                                         @RequestParam(required = false, defaultValue = "false") boolean forPassword) throws Exception {
        return ResponseEntity.ok(userService.checkEmail(emailCheckDto.email(), emailCheckDto.code(), forPassword));
    }

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호와 새로운 비밀번호를 보내주시면 비밀번호가 변경됩니다." +
            "<br>이메일 인증이 성공한 후 진행되어야합니다." +
            "<br>(409, \"UE010\", \"기존 비밀번호와 동일합니다.\")" +
            "<br>(409, \"UE005\", \"이메일 인증이 되지 않은 이메일입니다.\") - 인증번호가 발송되지않은 이메일" +
            "<br>(409, \"UE009\", \"인증번호가 만료되었습니다.\")")
    @PutMapping("/password")
    public ResponseEntity<RestResponseSimple> updatePassword(@RequestBody UpdatePasswordRequestDto updatePasswordRequestDto) throws Exception {
        return ResponseEntity.ok(userService.updatePassword(updatePasswordRequestDto));
    }

    @Operation(summary = "회원정보 조회", description = "access token을 헤더에 담아 주시면 회원정보가 반환됩니다.")
    @GetMapping
    public ResponseEntity<RestResponse<UserDataResponseDto>> getUser() throws Exception {
        return ResponseEntity.ok(userService.getUser());
    }

    @Operation(summary = "token refresh", description = "refresh token을 보내주시면 access token과 refresh token이 새로 발급됩니다.")
    @PostMapping("/refresh")
    public ResponseEntity<RestResponse<TokenResponseDto>> refresh(@RequestBody RefreshRequestDto refreshRequestDto) throws Exception {
        return ResponseEntity.ok(userService.refresh(refreshRequestDto.refresh()));
    }

    @Operation(summary = "프로필 이미지 업로드", description = "프로필 이미지를 업로드합니다. 프로필 사진 설정, 수정 공용 API 입니다." +
            "<br> 업로드가 완료되면 업로드된 이미지의 링크가 반환됩니다." +
            "<br>(500, \"SE002\", \"파일 업로드에 실패했습니다.\")" +
            "<br>(400, \"FE001\", \"파일이 존재하지 않습니다.\")")
    @PutMapping(path = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<RestResponse<ProfileImageResponseDto>> updateProfileImage(@RequestPart(value = "profileImage") MultipartFile profileImage) throws Exception {
        return ResponseEntity.ok(userService.updateProfileImage(profileImage));
    }

    @Operation(summary = "닉네임 수정", description = "닉네임을 수정합니다." +
            "<br>(409, \"UE008\", \"이미 존재하는 닉네임입니다.\")")
    @PutMapping("/nickname")
    public ResponseEntity<RestResponseSimple> updateNickname(@RequestBody UpdateNicknameRequestDto updateNicknameRequestDto) throws Exception {
        return ResponseEntity.ok(userService.updateNickname(updateNicknameRequestDto));
    }

}
