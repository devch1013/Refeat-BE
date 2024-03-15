package com.audrey.refeat.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.util.Arrays;

@Getter
public enum ErrorCode {
    AUTH_SUCCESS(200, "AUTH_SUCCESS", "인증 성공"),
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증되지 않았습니다."),
    FORBIDDEN(403, "FORBIDDEN", "권한이 없습니다."),
    USER_NOT_FOUND(401, "UE001", "사용자를 찾을 수 없습니다."),
    PASSWORD_NOT_MATCHED(401, "UE002", "비밀번호가 일치하지 않습니다."),
    EXISTED_EMAIL(409, "UE003", "이미 가입된 이메일입니다."),
    REGISTERED_WITH_ANOTHER_PROVIDER(409, "UE004", "다른 로그인 방식으로 이미 가입되어 있습니다."),
    EMAIL_VERIFICATION_NOT_PERMITTED(409, "UE005", "이메일 인증이 되지 않은 이메일입니다."),
    EMAIL_VERIFICATION_FAILED(409, "UE006", "인증번호가 일치하지 않습니다."),
    EMAIL_ALREADY_VERIFIED(409, "UE007", "이미 인증된 이메일입니다."),
    EXISTED_NICKNAME(409, "UE008", "이미 존재하는 닉네임입니다."),
    EMAIL_VERIFICATION_EXPIRED(409, "UE009", "인증번호가 만료되었습니다."),
    SAME_PASSWORD(409, "UE010", "기존 비밀번호와 동일합니다."),

    /////////////// token error
    TOKEN_NOT_FOUND(401, "TE001", "토큰이 존재하지 않습니다."),
    TOKEN_EXPIRED(401, "TE003", "만료된 토큰입니다."),
    TOKEN_NOT_SUPPORTED(401, "TE004", "지원하지 않는 토큰입니다."),
    TOKEN_NOT_VALID(401, "TE005", "유효하지 않은 토큰입니다."),
    REFRESH_TOKEN_EXPIRED(401, "TE006", "만료된 refresh 토큰입니다."),
    /////////////// email error
    EMAIL_SEND_FAILED(500, "EE001", "이메일 발송에 실패했습니다."),
    ////////////// server error
    INTERNAL_SERVER_ERROR(500, "SE001", "백엔드 내부 서버 에러입니다."),

    /////////////// request error
    INVALID_INPUT_VALUE(400, "RE001", "입력값이 올바르지 않습니다."),
    HTTP_REQUEST_METHOD_NOT_SUPPORTED(405, "RE002", "지원하지 않는 HTTP 메서드입니다."),
    HTTP_MESSAGE_NOT_READABLE(400, "RE003", "Request body가 필요합니다."),
    INVALID_UUID_VALUE(400, "RE004", "UUID 형식이 올바르지 않습니다."),
    PARAMETER_TYPE_INVALID(400, "RE005", "Parameter 형식이 잘못되었습니다."),


    /////////////// chat error
    MENTION_ID_INVALID(400, "CE001", "멘션 아이디가 올바르지 않습니다."),
    AI_NOT_MENTIONED(400, "CE002", "AI 멘션이 되어있지 않습니다."),

    /////////////// S3 error
    FILE_UPLOAD_FAIL(500, "SE002", "파일 업로드에 실패했습니다."),
    ////////////// File error
    FILE_NOT_EXIST(400, "FE001", "파일이 존재하지 않습니다."),
    ONLY_UPLOAD_ONE_TYPE(400, "FE002", "파일과 링크 중 하나만 업로드해주세요."),
    FILE_TYPE_NOT_MATCHED(400, "FE003", "파일 타입이 일치하지 않습니다."),

    //////////////////// project error
    PROJECT_NOT_EXIST(400, "PE001", "프로젝트가 존재하지 않습니다."),
    DOCUMENT_NOT_EXISTED(400, "DE001", "Document가 존재하지 않습니다."),
    DOCUMENT_NOT_BELONG_TO_PROJECT(400, "DE002", "해당 프로젝트에 속하지 않는 문서입니다."),
    COLUMN_TITLE_NOT_EXISTED(400, "CE001", "ColumnTitle이 존재하지 않습니다."),
    COLUMN_VALUE_NOT_EXISTED(400, "CE002", "ColumnValue가 존재하지 않습니다."),
    COLUMN_MAX_EXCEEDED(400, "CE003", "Column 최대 개수를 초과했습니다."),
    DOCUMENT_EMBEDDING_NOT_DONE(400, "DE003", "Document Embedding이 완료되지 않았습니다."),


    //////////////////// AI server error
    AI_SERVER_ERROR(500, "AE001", "AI 서버 에러입니다.");

    private final String message;
    private final String code;
    private final int status;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ErrorCode of(String code) {
        return Arrays.stream(values())
                .filter(errorCode -> errorCode.getCode().equals(code))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 ErrorCode 입니다."));
    }
}
