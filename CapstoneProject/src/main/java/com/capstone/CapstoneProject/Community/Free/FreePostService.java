package com.capstone.CapstoneProject.Community.Free;

import com.capstone.CapstoneProject.Community.Free.DTO.FreeBoardCreateDTO;
import com.capstone.CapstoneProject.Community.Free.DTO.FreeBoardListDTO;
import com.capstone.CapstoneProject.Community.Free.Entity.FreeBoard;
import com.capstone.CapstoneProject.Community.Free.Repository.FreeBoardRepository;
import com.capstone.CapstoneProject.Member.Member;
import com.capstone.CapstoneProject.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FreePostService {
    private final MemberRepository memberRepository;
    private final FreeBoardRepository freeBoardRepository;

     public void savePosting(FreeBoardCreateDTO freeBoardCreateDTO, String userName) {
         Member writer = memberRepository.findByUserID(userName);
         FreeBoard freeBoard = freeBoardCreateDTO.toEntity(writer);
         freeBoardRepository.save(freeBoard);
     }

    public Page<FreeBoardListDTO> getList(int page, String keyword) {
        int realPage = (page <= 0) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(realPage, 6,
                Sort.Direction.DESC, "createdDate");
        Page<FreeBoard> paging;

        if(keyword == null || keyword.isBlank()) {
            paging = freeBoardRepository.findAll(pageable);
        }else if(keyword.length() >= 2){
            pageable = PageRequest.of(realPage, 5);
            keyword = keyword.trim().replace(" ", "%");
            paging = freeBoardRepository.fullTextSearch(keyword, pageable);
        }else{
            paging = freeBoardRepository.findByTitleContaining(keyword, pageable);
        }

        return paging.map(free -> new FreeBoardListDTO(
                free.getId(),
                free.getTitle(),
                free.getContent(),
                free.getViewCount(),
                free.getWriter().getProfileName(),
                free.getCreatedDate(),
                free.getModifiedDate()
        ));
    }

}
