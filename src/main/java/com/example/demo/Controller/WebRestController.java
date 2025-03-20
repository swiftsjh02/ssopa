package com.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class WebRestController {
    private final Environment env;

    //이 클래스는 서버가 현재 production 프로파일인지 local 프로파일인지 알려주는 컨트롤러를 구현한 컨트롤러임.


    @GetMapping("/profile")
    public String getProfile(){
        return Arrays.stream(env.getActiveProfiles()).findFirst().orElse("");
    }
}
