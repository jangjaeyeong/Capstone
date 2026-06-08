package com.capstone.CapstoneProject.Community.Board;

import com.capstone.CapstoneProject.Community.Board.DTO.*;
import com.capstone.CapstoneProject.Community.CommonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@Component
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommonService commonService;
    // 커뮤니티 등록
    @PostMapping("api/community/posts")
    ResponseEntity<String> createFreeBoard(@Valid @RequestBody BoardCreateDTO boardCreateDTO,
                                           @AuthenticationPrincipal UserDetails loginUser) {
        commonService.checkUser(loginUser);
        String writer = loginUser.getUsername();
        postService.savePosting(boardCreateDTO, writer);

        return ResponseEntity.ok().body("등록 완료!");
    }
    //커뮤니티 리스트
    @GetMapping("api/community/posts")
    ResponseEntity<Page<BoardListDTO>> listFreeBoard(@ModelAttribute ListParamsDTO listParamsDTO) {
        System.out.println("뭔데");
        System.out.println("keyword" + listParamsDTO.getKeyword());
        System.out.println("category" + listParamsDTO.getCategory());
        if(listParamsDTO.getKeyword()!= null)  listParamsDTO.setKeyword(listParamsDTO.getKeyword().trim());
        if(listParamsDTO.getCategory().trim()!= null) listParamsDTO.setCategory(listParamsDTO.getCategory().trim());
        Page<BoardListDTO> paging = postService.getList(listParamsDTO.getPage(), listParamsDTO.getKeyword(), listParamsDTO.getCategory());

        return ResponseEntity.ok(paging);
    }
    //게시글 상세 페이지
    @GetMapping("api/community/posts/{postId}")
    ResponseEntity<BoardListDTO> boardDetail(@PathVariable Long postId) {
        BoardListDTO dto = postService.boardDetails(postId);

        return ResponseEntity.ok().body(dto);
    }

    //커뮤니티 삭제
    @DeleteMapping("api/community/posts/{postId}")
    ResponseEntity<String> deletePosting(@PathVariable Long postId,
                                         @AuthenticationPrincipal UserDetails loginUser) {
        commonService.checkUser(loginUser);
        postService.deletePosting(postId, loginUser.getUsername());

        return ResponseEntity.ok().body("게시글이 삭제되었습니다.");
    }
    //커뮤니티 게시글 수정
    @PatchMapping("api/community/posts/{postId}")
    ResponseEntity<String> editPosting(@RequestBody BoardEditDTO boardEditDTO, @PathVariable Long postId,
                                       @AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        postService.editPosting(boardEditDTO, postId, user.getUsername());
        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }
    //댓글 작성
    @PostMapping("api/community/posts/{postId}/comments")
    ResponseEntity<String> writeComments(@RequestBody WriteCommentsDTO commentsDTO,
                                         @PathVariable Long postId,
                                         @AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        postService.writeComments(commentsDTO, user.getUsername(), postId);
        return ResponseEntity.ok().body("댓글이 등록되었습니다.");
    }
    //추천 수 증가
    @PostMapping("api/community/posts/{postId}/like")
    ResponseEntity<String> onClickRecommend(@PathVariable Long postId,
                                            @AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        postService.onClickRecommend(postId);
        return ResponseEntity.ok().body("200");
    }
    //내 활동 목록
    @GetMapping("/api/community/me/activity")
    ResponseEntity<MyActivityResponseDTO> myActivity(@AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        MyActivityResponseDTO activityResponse = postService.myActivity(user.getUsername());
        return ResponseEntity.ok(activityResponse);
    }
}
