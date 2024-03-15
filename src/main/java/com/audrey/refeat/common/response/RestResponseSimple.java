package com.audrey.refeat.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RestResponseSimple {
    @Schema(example = "200")
    private int status;
    @Schema(example = "success")
    private String message;

    public static RestResponseSimple success() {
        return RestResponseSimple.builder()
                .status(200)
                .message("success")
                .build();
    }

    public static RestResponseSimple fail() {
        return RestResponseSimple.builder()
                .status(400)
                .message("fail")
                .build();
    }
}
