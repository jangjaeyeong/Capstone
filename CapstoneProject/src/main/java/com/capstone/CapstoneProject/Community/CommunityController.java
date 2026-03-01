package com.capstone.CapstoneProject.Community;

import com.capstone.CapstoneProject.Community.TeamProject.*;
import com.capstone.CapstoneProject.Member.Login.CustomUser;
import com.capstone.CapstoneProject.Member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Component
public class CommunityController {
    private final ProjectService projectService;


    @GetMapping("api/listProject")
    ResponseEntity<Page<ProjectListDTO>> listProject(@RequestParam(value="page",
            defaultValue = "1")int pageNumber) {
        Page<ProjectListDTO> paging = projectService.getList(pageNumber);

        return ResponseEntity.ok(paging);

    }
    @PostMapping("api/CreateProject")
    ResponseEntity<String> createTeamProject(@Valid @RequestBody ProjectCreateDTO projectCreateDTO,
                                             @AuthenticationPrincipal CustomUser customUser) {
        if(customUser == null) {
            throw new RuntimeException("회원 정보가 없습니다");
        }
        Member writer = customUser.getMember();
        projectService.saveProject(projectCreateDTO, writer);

        return ResponseEntity.ok().body("등록 완료!");
    }

    @GetMapping("api/projects/{id}")
    ResponseEntity<ProjectDetailDTO> details(@PathVariable int id) {
        ProjectDetailDTO detail = projectService.detailProject(id);
        return ResponseEntity.ok().body(detail);
    }

    @PatchMapping("api/editProject/{id}")
    ResponseEntity<String>modifiedProject(@RequestBody ProjectEditDTO editDTO,
                                          @PathVariable int id,
                                          @AuthenticationPrincipal CustomUser loginUser) {
        if(loginUser == null) {
            new IllegalArgumentException("로그인이 필요합니다.");
        }
        projectService.editProject(editDTO, id, loginUser.getUsername());
        return ResponseEntity.ok().body("수정 완료");
    }
    @DeleteMapping("api/teamproject/{projectId}")
    ResponseEntity<String> deleteProject(@PathVariable int projectId,
                                         @AuthenticationPrincipal CustomUser loginUser) {
        if(loginUser == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }
        projectService.deleteProject(projectId, loginUser.getUsername());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }
    @PostMapping("api/projects/{id}/join")
    public ResponseEntity<String> joinProject(@PathVariable int id,
                                              @AuthenticationPrincipal CustomUser user) {
        if(user == null) {
            return  ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
        }
        projectService.joinedProject(id, user.getMember());
        return ResponseEntity.ok("참가 신청이 완료되었습니다.");
    }
}
