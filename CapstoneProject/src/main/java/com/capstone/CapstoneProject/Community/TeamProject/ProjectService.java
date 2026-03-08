package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;


@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final TeamProjectRepository teamProjectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    //전체 리스트
     public Page<ProjectListDTO> getList(int page, String keyword) {
         int realPage = (page <= 0) ? 0 : page - 1;
         Pageable pageable = PageRequest.of(realPage, 5,
                 Sort.Direction.DESC, "createdDate");
         Page<TeamProject> paging;
         if(keyword == null || keyword.isBlank()) {
             paging = teamProjectRepository.findAll(pageable);
         }else if(keyword.length() >= 2){
              pageable = PageRequest.of(realPage, 5);
             keyword = keyword.trim().replace(" ", "%");
             paging = teamProjectRepository.fullTextSearch(keyword, pageable);
         }else{
             paging = teamProjectRepository.findByTitleContaining(keyword, pageable);
         }

         return paging.map(project -> new ProjectListDTO(
                 project.getCategory(),
                 project.getUserLimit(),
                 project.getTitle(),
                 project.getState(),
                 project.getWriter().getProfileName(),
                 project.getCreatedDate(),
                 project.getModifiedDate()
         ));
     }
     //프로젝트 생성
    public void saveProject(ProjectCreateDTO projectCreateDTO, Member writer) {
        TeamProject teamProject = projectCreateDTO.toEntity(writer);
        teamProjectRepository.save(teamProject);

        ProjectMember projectMember = ProjectMember.builder()
                        .user(writer)
                        .project(teamProject)
                        .role("팀장")
                        .build();
        projectMemberRepository.save(projectMember);
    }

    //프로젝트 상세 페이지
    @Transactional(readOnly = true)
    public ProjectDetailDTO detailProject(@PathVariable int id) { //고유 ID
        TeamProject tp =  teamProjectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 삭제되었습니다."));
        tp.setViewCount(tp.getViewCount() + 1);
        return new ProjectDetailDTO(tp);
    }

    // 프로젝트 수정
    public void editProject(ProjectEditDTO editDTO, int id, String loginUser) {
        TeamProject tp = teamProjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제된 게시글입니다."));
        if(tp.getWriter().getUserID().equals(loginUser)) {
            tp.EditProject(editDTO);
        }else {
            throw new IllegalArgumentException("작성자만 게시글을 수정할 수 있습니다.");
        }
    }

    //프로젝트 삭제
    public void deleteProject(int projectId, String loginUser) {
         TeamProject tp = teamProjectRepository.findById(projectId)
                 .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
         if(tp.getWriter().getUserID().equals(loginUser)) {
             teamProjectRepository.delete(tp);
         }else {
             throw new IllegalArgumentException("작성자만 게시글을 삭제할 수 있습니다.");
         }
    }

    public void joinedProject(int projectId, Member loginUser) {
        TeamProject tp = teamProjectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
        boolean alreadyJoined = projectMemberRepository
                .existsByUserAndProject(loginUser, tp);
        if(alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 프로젝트입니다!!");
        }
        long totalRaws = projectMemberRepository.countByProject_id(tp.getId());
        TeamProject teamProject = new TeamProject();

        if(totalRaws > teamProject.getUserLimit()) {
            ProjectMember projectMember = ProjectMember.builder()
                    .user(loginUser)
                    .project(tp)
                    .role("팀원")
                    .build();
            projectMemberRepository.save(projectMember);
        }else{
            throw new IllegalArgumentException("모집이 마감되었습니다");
        }
        }
    }
