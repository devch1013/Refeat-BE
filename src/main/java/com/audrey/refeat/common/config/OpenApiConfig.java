package com.audrey.refeat.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()

                .title("Refeat. API")
                .description("Refeat swagger page" +
                        "<h3>Last Update 01.29 13:17</h3>" +

                        "<br>아래는 모든 엔드포인트에서 발생할 수 있는 에러코드 목록입니다. " +
                        "<br>{\"status\":409,\"code\":\"UE003\",\"message\":\"이미 존재하는 이메일입니다.\"} 형태로 반환됩니다." +
                        "<br><br>" +
                        "TOKEN_NOT_FOUND(401, \"TE001\", \"토큰이 존재하지 않습니다.\")<br>" +
                        "INVALID_TOKEN(401, \"TE002\", \"유효하지 않은 토큰입니다.\")<br>" +

                        "TOKEN_EXPIRED(401, \"TE003\", \"만료된 토큰입니다.\")<br>" +
                        "TOKEN_NOT_SUPPORTED(401, \"TE004\", \"지원하지 않는 토큰입니다.\")<br>" +
                        "INTERNAL_SERVER_ERROR(500, \"SE001\", \"백엔드 내부 서버 에러입니다.\")<br>" +
                        "INVALID_INPUT_VALUE(400, \"RE001\", \"입력값이 올바르지 않습니다.\") - request parameter 문제<br>" +
                        "HTTP_REQUEST_METHOD_NOT_SUPPORTED(405, \"RE002\", \"지원하지 않는 HTTP 메서드입니다.\")<br>");

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components().addSecuritySchemes(securitySchemeName, new SecurityScheme().name(securitySchemeName).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(info);
    }
}

