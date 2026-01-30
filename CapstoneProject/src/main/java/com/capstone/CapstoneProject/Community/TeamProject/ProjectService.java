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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {
    private final TeamProjectRepository teamProjectRepository;

     public Page<ProjectListDTO> getList(int page) {
         int realPage = (page <= 0) ? 0 : page - 1;
         Pageable pageable = PageRequest.of(realPage+1, 5,
                 Sort.Direction.DESC, "modifiedDate");
         Page<TeamProject> paging = teamProjectRepository.findAll(pageable);

         Page<ProjectListDTO> listDto = paging.map(project -> new ProjectListDTO(
                 project.getField(),
                 project.getMaxPersonnel(),
                 project.getTitle(),
                 project.getState(),
                 project.getWriter().getProfileName(),
                 project.getCreatedDate(),
                 project.getModifiedDate()
         ));
         return listDto;
     }
    public void saveProject(ProjectCreateDTO projectCreateDTO, Member writer) {
        TeamProject teamProject = projectCreateDTO.toEntity(writer);
        teamProjectRepository.save(teamProject);

    }
    @Transactional(readOnly = true)
    public ProjectDetailDTO detailProject(@PathVariable int id) { //고유 ID
        TeamProject tp =  teamProjectRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 삭제되었습니다."));
        tp.setViewCount(tp.getViewCount() + 1);
        return new ProjectDetailDTO(tp);
    }

    public void editProject(ProjectEditDTO editDTO, int id) {
        TeamProject tp = teamProjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("삭제된 게시물입니다."));
        tp.EditProject(editDTO);
    }
}
