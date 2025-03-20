package com.example.demo.Controller;

import com.example.demo.Service.MemberService;
import com.example.demo.common.HttpResponseUtil;
import com.example.demo.dto.member.ChangePasswordRequestDto;
import com.example.demo.dto.member.MemberResponseDto;

import com.example.demo.repository.DeviceTokenRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Api(tags = "MemberController : 회원 정보 요청 관련 컨트롤러")
public class MemberController {
    private final MemberService memberService;
    private final HttpResponseUtil httpResponseUtil;

    @GetMapping("/me")
    @ApiOperation(value = "내 정보 조회")
    public ResponseEntity<?> getMyMemberInfo() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        try {
            return httpResponseUtil.createOKHttpResponse(myInfoBySecurity, "내 정보 조회 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("내 정보 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/nickname")
    @ApiOperation(value = "닉네임 변경 요청")
    public ResponseEntity<?> setMemberNickname() {
        try {
            return httpResponseUtil.createOKHttpResponse(memberService.changeMemberNickname(), "닉네임 변경 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("닉네임 변경 실패: " + e.getMessage());
        }
    }

    @ApiOperation(value = "비밀번호 변경 요청")
    @PostMapping("/password")
    public ResponseEntity<?> setMemberPassword(@RequestBody ChangePasswordRequestDto request) {
        try {
            return httpResponseUtil.createOKHttpResponse(memberService.changeMemberPassword(request.getEmail(), request.getExPassword(), request.getNewPassword()), "비밀번호 변경 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("비밀번호 변경 실패: " + e.getMessage());
        }
    }

    @PostMapping("/synctoken")
    @ApiOperation(value = "모바일 기기의 토큰을 계정과 연동")
    public ResponseEntity<?> syncToken(@RequestParam(value="deviceToken") String token) {
        try {
            return httpResponseUtil.createOKHttpResponse(memberService.syncToken(token), "토큰연동 성공 ");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("토큰 연동 실패: " + e.getMessage());
        }
    }



    /**
     * CloudFlare r2 에 이미지 업로드
     * @return 성공 시 200 Success와 함께 업로드 된 파일의 파일명 리스트 반환
     */
//    @ApiOperation(value = "CloudFlare r2에 이미지 업로드", notes = "CloudFlare r2에 이미지 업로드 ")
//    @PostMapping("/editProfileImage")
//    public ResponseEntity<String> uploadImage(@ApiParam(value="img 파일()", required = true) @RequestPart MultipartFile multipartFile) {
//        return (ResponseEntity<String>) httpResponseUtil.createOKHttpResponse(clfrR2Service.uploadImage(multipartFile), "이미지 업로드 성공");
//    }
}