package com.example.demo.Controller;

import com.example.demo.Service.MemberService;
import com.example.demo.common.HttpResponseUtil;
import com.example.demo.config.SecurityUtil;
import com.example.demo.dto.member.*;

import com.example.demo.entity.Member.Friendship;
import com.example.demo.entity.Member.Member;
import com.example.demo.repository.Member.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final HttpResponseUtil httpResponseUtil;
    private final FriendshipRepository friendshipRepository;


    @GetMapping("/me")
    public ResponseEntity<?> getMyMemberInfo() {
        MemberResponseDto myInfoBySecurity = memberService.getMyInfoBySecurity();
        try {
            return httpResponseUtil.createOKHttpResponse(myInfoBySecurity, "내 정보 조회 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("내 정보 조회 실패: " + e.getMessage());
        }
    }

    @GetMapping("/friend/list")
    public ResponseEntity<?> getMyFriends() {
        try {


            return httpResponseUtil.createOKHttpResponse(memberService.getMyFriends(), "친구 목록 조회 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("친구 목록 조회 실패: " + e.getMessage());
        }
    }

    @PostMapping("/friend/request")
    public ResponseEntity<?> requestFriend(@RequestBody FriendRequestDto dto) {
        try {
                    memberService.sendFriendRequest(dto);
            return httpResponseUtil.createOKHttpResponse(null, "친구 요청이 전송되었습니다.");
        } catch (IllegalStateException e){
            return httpResponseUtil.createBadRequestHttpResponse(e.getMessage());
        }
        catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("친구 요청 실패: " + e.getMessage());
        }
    }

    @PostMapping("/friend/reply")
    public ResponseEntity<?> requestFriendReply(@RequestBody FriendRequestReplyDto dto) {
        try{
            memberService.handleFriendRequestReply(dto);
            return httpResponseUtil.createOKHttpResponse(null, "친구 요청 수락 완료");
        }catch (IllegalStateException e){
            return httpResponseUtil.createBadRequestHttpResponse(e.getMessage());
        }catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse(e.getMessage());
        }
    }

    @GetMapping("/friend/lookup")
    public ResponseEntity<?> getFriendRequest() {
        try{
            List<FriendshipRequestLookupDto> requests = memberService.getPendingRequestsForUser();
            return httpResponseUtil.createOKHttpResponse(requests,"친구 요청 목록 조회 성공");
        }catch (Exception e){
            return httpResponseUtil.createInternalServerErrorHttpResponse(e.getMessage());
        }
    }


    @PostMapping("/nickname")
    public ResponseEntity<?> setMemberNickname() {
        try {
            return httpResponseUtil.createOKHttpResponse(memberService.changeMemberNickname(), "닉네임 변경 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("닉네임 변경 실패: " + e.getMessage());
        }
    }

    @PostMapping("/password")
    public ResponseEntity<?> setMemberPassword(@RequestBody ChangePasswordRequestDto request) {
        try {
            return httpResponseUtil.createOKHttpResponse(memberService.changeMemberPassword(request.getEmail(), request.getExPassword(), request.getNewPassword()), "비밀번호 변경 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("비밀번호 변경 실패: " + e.getMessage());
        }
    }

    @PostMapping("/synctoken")
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