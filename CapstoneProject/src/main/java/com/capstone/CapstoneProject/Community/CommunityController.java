package com.capstone.CapstoneProject.Community;

import com.capstone.CapstoneProject.Community.Board.DTO.BoardCreateDTO;
import com.capstone.CapstoneProject.Community.Board.DTO.BoardListDTO;
import com.capstone.CapstoneProject.Community.Board.PostService;
import com.capstone.CapstoneProject.Community.TeamProject.*;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectCreateDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectEditDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectJoinDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Component
public class CommunityController {
    private final ProjectService projectService;
    private final PostService postService;

    //팀 프로젝트 리스트 API
    @GetMapping("api/list")
    ResponseEntity<Page<ProjectListDTO>> listProject(@RequestParam(value="page",
            defaultValue = "1")int pageNumber, @RequestParam
            (value = "keyword", required = false)String keyword) {
        if(keyword!= null) keyword = keyword.trim();
        Page<ProjectListDTO> paging = projectService.getList(pageNumber, keyword);

        return ResponseEntity.ok(paging);
    }
    //팀 프로젝트 생성 API
    @PostMapping("api/teamproject")
    ResponseEntity<String> createTeamProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO,
                                             @AuthenticationPrincipal UserDetails loginUser) {
        if(loginUser == null) {
            throw new RuntimeException("회원 정보가 없습니다");
        }
        String writer = loginUser.getUsername();
        projectService.saveProject(projectCreateDTO, writer);

        return ResponseEntity.ok().body("등록 완료!");
    }

    //팀프로젝트 상세 페이지 API
    @GetMapping("api/projects/{id}")
    ResponseEntity<ProjectDetailDTO> details(@PathVariable Long id) {
        ProjectDetailDTO detail = projectService.detailProject(id);
        return ResponseEntity.ok().body(detail);
    }

    //팀 프로젝트 수정 API
    @PatchMapping("api/teamproject/{id}")
    ResponseEntity<String>modifiedProject(@RequestBody ProjectEditDTO editDTO,
                                          @PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails loginUser) {
        if(loginUser == null) {
            throw new IllegalArgumentException("로그인이 필요합니다.");
        }
        projectService.editProject(editDTO, id, loginUser.getUsername());
        return ResponseEntity.ok().body("수정 완료");
    }

    //팀 프로젝트 삭제 API
    @DeleteMapping("api/teamproject/{projectId}")
    ResponseEntity<String> deleteProject(@PathVariable Long projectId,
                                         @AuthenticationPrincipal UserDetails loginUser) {
        if(loginUser == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }
        projectService.deleteProject(projectId, loginUser.getUsername());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }
    //팀 프로젝트 참가 API
    @PostMapping("api/teamproject/join")
    public ResponseEntity<String> joinProject(@RequestBody ProjectJoinDTO projectJoinDTO,
                                              @AuthenticationPrincipal UserDetails user) {
        if(user == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }
        projectService.joinedProject(projectJoinDTO, user.getUsername());
        return ResponseEntity.ok("참가 신청이 완료되었습니다.");
    }
    @DeleteMapping("api/teamproject/leave")
    public ResponseEntity<String> leaveProject(@RequestBody Map<String, Long> projectId,
                                               @AuthenticationPrincipal UserDetails user){
        Long id = projectId.get("projectId");
        projectService.leaveProject(id, user.getUsername());
        return ResponseEntity.ok("참여가 취소되었습니다.");
    }

    // 자유 게시판 등록
    @PostMapping("api/community/posts")
    ResponseEntity<String> createFreeBoard(@Valid @RequestBody BoardCreateDTO boardCreateDTO,
                                             @AuthenticationPrincipal UserDetails loginUser) {
        if(loginUser == null) {
            throw new RuntimeException("회원 정보가 없습니다");
        }
        String writer = loginUser.getUsername();
        postService.savePosting(boardCreateDTO, writer);

        return ResponseEntity.ok().body("등록 완료!");
    }
    //자유게시판 리스트
     @GetMapping("api/community/posts")
     ResponseEntity<Page<BoardListDTO>> listFreeBoard(@RequestParam(value="page",
             defaultValue = "1")int pageNumber, @RequestParam
             (value = "keyword", required = false)String keyword) {
         if(keyword!= null) keyword = keyword.trim();
         Page<BoardListDTO> paging = postService.getList(pageNumber, keyword);

         return ResponseEntity.ok(paging);
     }

}
