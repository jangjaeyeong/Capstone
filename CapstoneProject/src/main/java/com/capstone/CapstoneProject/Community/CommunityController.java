package com.capstone.CapstoneProject.Community;

import com.capstone.CapstoneProject.Community.TeamProject.ProjectDetailDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ProjectEditDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ProjectCreateDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ProjectService;
import com.capstone.CapstoneProject.Member.Login.CustomUser;
import com.capstone.CapstoneProject.Member.Member;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Component
public class CommunityController {
    private final ProjectService projectService;

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
                                          @PathVariable int id) {
        projectService.editProject(editDTO, id);

        return ResponseEntity.ok().body("수정 완료");
    }


}
