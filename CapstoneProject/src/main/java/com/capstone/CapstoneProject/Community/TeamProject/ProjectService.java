package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectCreateDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectEditDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.ProjectJoinDTO;
import com.capstone.CapstoneProject.Community.TeamProject.RequestDTO.RecruitmentsDTO;
import com.capstone.CapstoneProject.Community.TeamProject.ResponseDTO.*;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectMember;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectRoles;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.Tags;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import com.capstone.CapstoneProject.Community.TeamProject.Repository.ProjectMemberRepository;
import com.capstone.CapstoneProject.Community.TeamProject.Repository.ProjectRolesRepository;
import com.capstone.CapstoneProject.Community.TeamProject.Repository.TagsRepository;
import com.capstone.CapstoneProject.Community.TeamProject.Repository.TeamProjectRepository;
import com.capstone.CapstoneProject.Member.Member;
import com.capstone.CapstoneProject.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final TeamProjectRepository teamProjectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TagsRepository tagsRepository;
    private final ProjectRolesRepository projectRolesRepository;
    private final MemberRepository memberRepository;


    public <T> Map<Long, List<String>> groupEntitiesListByProjectId(
            List<T> entities, Function<T, Long> extractProjectId,
            Function<T, String> extractEntityValues) {
        if(entities == null || entities.isEmpty()) {
            return new HashMap<>();
        }
        return entities.stream().collect(Collectors.groupingBy(extractProjectId,
                Collectors.mapping(extractEntityValues, Collectors.toList())));
    }

    //전체 리스트
     public Page<ProjectListDTO> getList(int page, String keyword) {
         int realPage = (page <= 0) ? 0 : page - 1;
         Pageable pageable = PageRequest.of(realPage, 6,
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

         List<Long> projectIds = paging.stream().map(TeamProject :: getId)
                 .collect(Collectors.toList());

         //같은 프로젝트 ID를 가진 태그를 리스트로 변환
         List<Tags> allTags = tagsRepository.findByProjectIdIn(projectIds);
         Map<Long, List<String>> tagMap = groupEntitiesListByProjectId(allTags,
                 tag -> tag.getProject().getId(), Tags::getTag);

         //같은 프로젝트 ID를 가진 직군들을 리스트로 변환
         List<ProjectRoles> needRoles = projectRolesRepository.findByProjectIdIn(projectIds);
         Map<Long, List<String>> needRolesMap = groupEntitiesListByProjectId(needRoles,
                 needRole -> needRole.getProject().getId(),
                 ProjectRoles::getNeedRoles);

         List<ProjectMember> projectMember = projectMemberRepository.findAll();
         Map<Long, List<ListMembersDTO>> membersDTO = projectMember.stream().collect(
                 Collectors.groupingBy(member -> member.getProject().getId(),
                 Collectors.mapping(member -> new ListMembersDTO(
                         member.getUser().getProfileName(), member.getAuthority().equals("팀장"),
                         member.getRole() != null ? member.getRole() : ""
                 ), Collectors.toList())));

         List<ProjectRoles> projectRoles = projectRolesRepository.findAll();
         Map<Long, List<ResRecruitmentDTO>> recruitmentDTO = projectRoles.stream()
                 .collect(Collectors.groupingBy((ProjectRoles projectRole) ->
                                 projectRole.getProject().getId(),
                         Collectors.mapping(projectRole ->
                                 new ResRecruitmentDTO(projectRole.getNeedRoles(),
                                         projectRole.getRecruitments()),
                                 Collectors.toList())));

         return paging.map(project -> new ProjectListDTO(
                 project.getId(),
                 project.getCategory(),
                 project.getUserLimit(),
                 project.getTitle(),
                 project.getContent(),
                 project.getState(),
                 project.getWriter().getProfileName(),
                 project.getCreatedDate(),
                 project.getModifiedDate(),
                 tagMap.getOrDefault(project.getId(), new ArrayList<>()),
                 needRolesMap.getOrDefault(project.getId(), new ArrayList<>()),
                 membersDTO.getOrDefault(project.getId(), new ArrayList<>()),
                 recruitmentDTO.getOrDefault(project.getId(), new ArrayList<>())
         ));
     }
     //프로젝트 생성
    public void saveProject(ProjectCreateDTO projectCreateDTO, String loginUser) {
         Member writer = memberRepository.findByUserID(loginUser);
        TeamProject teamProject = projectCreateDTO.toEntity(writer);
        teamProjectRepository.save(teamProject);

        ProjectMember projectMember = ProjectMember.builder()
                        .user(writer)
                        .project(teamProject)
                        .authority("팀장")
                        .role(projectCreateDTO.getMyRole())
                        .build();
        projectMemberRepository.save(projectMember);
        List<Tags> tagsEntityList = new ArrayList();
        for(String tag : projectCreateDTO.getTags()) {
            Tags tags = Tags.builder()
                    .project(teamProject)
                    .tag(tag)
                    .build();
            tagsEntityList.add(tags);
        }
        tagsRepository.saveAll(tagsEntityList);
        List<ProjectRoles> needRolesList = new ArrayList<>();

        for(RecruitmentsDTO recruitment : projectCreateDTO.getRecruitments()) {
            ProjectRoles projectRoles = ProjectRoles.builder()
                    .project(teamProject)
                    .needRoles(recruitment.getRole())
                    .recruitments(recruitment.getCount())
                    .build();
            needRolesList.add(projectRoles);
        }
        projectRolesRepository.saveAll(needRolesList);
    }

    //프로젝트 상세 페이지
    @Transactional(readOnly = true)
    public ProjectDetailDTO detailProject(@PathVariable Long id) { //고유 ID
        TeamProject tp =  teamProjectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 삭제되었습니다."));
        tp.increaseViewCount();

        List<Tags> tags = tagsRepository.findByProjectId(tp.getId());
        List<String> tagNames = tags.stream().map(Tags::getTag).collect(Collectors.toList());

        List<ProjectRoles> needRoles = projectRolesRepository.findByProjectId(tp.getId());
        List<String> roleNames = needRoles.stream().map(ProjectRoles::getNeedRoles).collect(
                Collectors.toList());
        List<ProjectMember> allMembers = projectMemberRepository.findByProjectId(tp.getId());
        List<ListMembersDTO> membersDTO = allMembers.stream().map(
                members -> new ListMembersDTO(members.getUser().getProfileName(),
                        members.getAuthority().equals("팀장"), members.getRole() != null ?
                        members.getRole():"")).collect(Collectors.toList());

        return new ProjectDetailDTO(tp, tagNames, roleNames, membersDTO);
    }

    // 프로젝트 수정
    public void editProject(ProjectEditDTO editDTO, Long id, String loginUser) {
        TeamProject tp = teamProjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제된 게시글입니다."));
        if(tp.getWriter().getUserID().equals(loginUser)) {
            tp.editProject(editDTO);
        }else {
            throw new IllegalArgumentException("작성자만 게시글을 수정할 수 있습니다.");
        }
    }

    //프로젝트 삭제
    public void deleteProject(Long projectId, String loginUser) {
         TeamProject tp = teamProjectRepository.findById(projectId)
                 .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
         if(tp.getWriter().getUserID().equals(loginUser)) {
             projectRolesRepository.deleteByProjectId(tp.getId());
             projectMemberRepository.deleteByProjectId(tp.getId());
             tagsRepository.deleteByProjectId(tp.getId());
             teamProjectRepository.delete(tp);
         }else {
             throw new IllegalArgumentException("작성자만 게시글을 삭제할 수 있습니다.");
         }
    }

    //프로젝트 참가
    public void joinedProject(ProjectJoinDTO projectJoinDTO, String loginUser) {
         Member member = memberRepository.findByUserID(loginUser);
        TeamProject tp = teamProjectRepository.findById(projectJoinDTO.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("없는 게시글입니다."));
            boolean alreadyJoined = projectMemberRepository
                    .existsByUserAndProject(member, tp);
            if(alreadyJoined) {
                throw new IllegalArgumentException("이미 참가한 프로젝트입니다!!");
            }
            long totalRaws = projectMemberRepository.countByProject_id(tp.getId());
            TeamProject teamProject = new TeamProject();

            if(totalRaws > teamProject.getUserLimit()) {
                ProjectMember projectMember = ProjectMember.builder()
                        .user(member)
                        .project(tp)
                        .authority("팀원")
                        .role(projectJoinDTO.getRole())
                        .build();
                projectMemberRepository.save(projectMember);
            }else if(totalRaws == teamProject.getUserLimit()){
                tp.editState(totalRaws);
            } else{
            throw new IllegalArgumentException("모집이 마감되었습니다");
        }

        }
        public void leaveProject(Long projectId, String user) {
            Member loginUser = memberRepository.findByUserID(user);
            ProjectMember member = projectMemberRepository
                    .findByProject_IdAndUser_ProfileName(projectId, loginUser.getProfileName()).orElseThrow(()
                    -> new IllegalArgumentException("참여중인 유저가 아닙니다."));
            projectMemberRepository.delete(member);
        }
    }
