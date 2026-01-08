package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final TeamProjectRepository teamProjectRepository;

    public void saveProject(ProjectCreateDTO projectCreateDTO, Member writer) {
        TeamProject teamProject = projectCreateDTO.toEntity(writer);
        teamProjectRepository.save(teamProject);

    }
    @Transactional(readOnly = true)
    public ProjectDetailDTO detailProject(@PathVariable int id) {
        TeamProject tp =  teamProjectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 삭제되었습니다."));
        tp.setViewCount(tp.getViewCount() + 1);
        return new ProjectDetailDTO(tp);
    }

    public void editProject(ProjectEditDTO editDTO, int id) {
        TeamProject tp = teamProjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제된 게시물입니다."));
        tp.EditProject(editDTO);
        System.out.println(tp.getId());
        System.out.println(tp.getLocation());
        System.out.println(tp.getField());
        System.out.println(tp.getMaxPersonnel());
        System.out.println(tp.getState());
        System.out.println(tp.getTitle());
        System.out.println(tp.getContent());
        System.out.println(tp.getWriter().getProfileName().toString());
        System.out.println(tp.getCreatedDate());
        System.out.println(tp.getModifiedDate());
        System.out.println(tp.getViewCount());
    }
}
