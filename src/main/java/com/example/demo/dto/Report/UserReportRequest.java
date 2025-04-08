package com.example.demo.dto.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReportRequest {
    //이 Dto 안에는 reportedUserId 와 신고내용인 content를 받는다.    @NotNull(message = "신고할 유저의 아이디 입력해주세요.")
    private Long reportedUserId;    @NotBlank(message = "신고 사유를 입력하세요.")
    private String content;

}

