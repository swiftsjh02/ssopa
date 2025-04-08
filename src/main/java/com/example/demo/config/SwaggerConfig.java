package com.example.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        String jwtSchemeName = "JWT";

        return new OpenAPI()
                .info(new Info()
                        .title("ADIGO 백엔드 API 명세")
                        .description("백엔드 API 사용 방법 등을 정리한 페이지 입니다")
                        .version("V1.0")
                        .contact(new Contact()
                                .name("서지호")
                                .url("홈페이지 URL")
                                .email("ksdk6145@gachon.ac.kr"))


                )
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))

                .servers(Collections.singletonList(
                        new Server().url("/api")
                )); //이 부분은 prod 환경에서만 쓰고 local에서 servers 부분은 지우고 써야함






    }
}
