package com.audrey.refeat.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T>{
    // 임의의 자료형 T 지정
    @Schema(example = "200")
    private int status;
    @Schema(example = "success")
    private String message;
    private T data;

    /**
     * 200 OK
     */
    public static<T> RestResponse<T> ok(final T data) {
        return RestResponse.<T>builder()
                .status(200)
                .message("success")
                .data(data).build();
    }


}