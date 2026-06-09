package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Community.CommonService;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectMember;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectCreateDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectEditDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectJoinDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectLeaveDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO.JoinedTeamProjectDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO.ProjectDetailDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO.ProjectListDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Component
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final CommonService commonService;
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
        commonService.checkUser(loginUser);
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
        commonService.checkUser(loginUser);
        projectService.editProject(editDTO, id, loginUser.getUsername());
        return ResponseEntity.ok().body("수정 완료");
    }

    //팀 프로젝트 삭제 API
    @DeleteMapping("api/teamproject/{projectId}")
    ResponseEntity<String> deleteProject(@PathVariable Long projectId,
                                         @AuthenticationPrincipal UserDetails loginUser) {
        commonService.checkUser(loginUser);
        projectService.deleteProject(projectId, loginUser.getUsername());
        return ResponseEntity.ok("게시글이 삭제되었습니다.");
    }
    //팀 프로젝트 참가 API
    @PostMapping("api/teamproject/join")
    public ResponseEntity<String> joinProject(@RequestBody ProjectJoinDTO projectJoinDTO,
                                              @AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        projectService.joinedProject(projectJoinDTO, user.getUsername());
        return ResponseEntity.ok("참가 신청이 완료되었습니다.");
    }
    @DeleteMapping("api/teamproject/leave")
    public ResponseEntity<String> leaveProject(@RequestBody ProjectLeaveDTO projectLeaveDTO,
                                               @AuthenticationPrincipal UserDetails user){
        Long id = projectLeaveDTO.getProjectId();
        projectService.leaveProject(id, user.getUsername());
        return ResponseEntity.ok("참여가 취소되었습니다.");
    }
    @GetMapping("/api/teamproject/joined")
    public ResponseEntity<List<JoinedTeamProjectDTO>> joinedProject(@AuthenticationPrincipal UserDetails user) {
        commonService.checkUser(user);
        List<JoinedTeamProjectDTO> dto = projectService.joinedProject(user.getUsername());
        return ResponseEntity.ok(dto);
    }


}
