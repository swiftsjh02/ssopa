package com.example.demo.Controller.Post;

import com.example.demo.Exception.Post.NoSufficientPermissionException;
import com.example.demo.Service.PostService;
import com.example.demo.common.HttpResponseUtil;
import com.example.demo.dto.post.*;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final PostService postService;
    private final HttpResponseUtil httpResponseUtil;

    @PostMapping("/add")
    public ResponseEntity<?> NewPost(@RequestBody PostRequestDto request) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.newpost(request.getTitle(), request.getContent(), request.getCategory()), "글 작성 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("글 작성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/modify/{id}")
    public ResponseEntity<?> updatepost(@RequestBody PostUpdateDto request, @PathVariable(name = "id") Long id) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.updatepost(request.getTitle(), request.getContent(), id), "게시글 수정 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("게시글 수정 실패: " + e.getMessage());
        }
    }

    @GetMapping("/loadinfo/{id}")
    public ResponseEntity<?> ReadPost(@PathVariable(name = "id") Long id) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.readpost(id), "게시글 정보 불러오기 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("게시글 정보 불러오기 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> DeletePost(@PathVariable(name = "id") Long id) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.deletepost(id), "게시글 삭제 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("게시글 삭제 실패: " + e.getMessage());
        }
    }

    @GetMapping("/load/{category}/{page}")
    public ResponseEntity<?> LoadPost(@PathVariable(name = "category") String category, @PathVariable(name = "page") int page) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.loadpost(category, page), "게시글 불러오기 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("게시글 불러오기 실패: " + e.getMessage());
        }
    }

    @GetMapping("/loadmy")
    public ResponseEntity<?> LoadMyPost() {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.myWritePost(), "내가 쓴 게시글 불러오기 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("내가 쓴 게시글 불러오기 실패: " + e.getMessage());
        }
    }

    @PostMapping("/heart/{id}")
    public ResponseEntity<?> HeartPost(@PathVariable(name = "id") Long id) {
        try {
            return httpResponseUtil.createOKHttpResponse(postService.heartpost(id), "게시글 좋아요 누르기 성공");
        } catch (Exception e) {
            return httpResponseUtil.createInternalServerErrorHttpResponse("게시글 좋아요 누르기 실패: " + e.getMessage());
        }
    }
    @GetMapping("/hot")
    public ResponseEntity<?> HotDiary() {

        try {
            return httpResponseUtil.createOKHttpResponse(postService.getHotList(), "인기 게시글 불러오기 성공");
        }catch (Exception e){
            return httpResponseUtil.createInternalServerErrorHttpResponse("인기 게시글 불러오기 실패: " + e.getMessage());
        }
    }
}