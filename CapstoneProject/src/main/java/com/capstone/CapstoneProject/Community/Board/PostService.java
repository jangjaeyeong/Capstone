package com.capstone.CapstoneProject.Community.Board;

import com.capstone.CapstoneProject.Community.Board.DTO.BoardCreateDTO;
import com.capstone.CapstoneProject.Community.Board.DTO.BoardEditDTO;
import com.capstone.CapstoneProject.Community.Board.DTO.BoardListDTO;
import com.capstone.CapstoneProject.Community.Board.Entity.Board;
import com.capstone.CapstoneProject.Community.Board.Repository.BoardRepository;
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

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;

    public void savePosting(BoardCreateDTO boardCreateDTO, String userName) {
        Member writer = memberRepository.findByUserID(userName);
        Board freeBoard = boardCreateDTO.toEntity(writer);
        boardRepository.save(freeBoard);
    }

    public Page<BoardListDTO> getList(int page, String keyword) {
        int realPage = (page <= 0) ? 0 : page - 1;
        Pageable pageable = PageRequest.of(realPage, 10,
                Sort.Direction.DESC, "createdDate");
        Page<Board> paging;

        if (keyword == null || keyword.isBlank()) {
            paging = boardRepository.findAll(pageable);
        } else if (keyword.length() >= 2) {
            pageable = PageRequest.of(realPage, 5);
            keyword = keyword.trim().replace(" ", "%");
            paging = boardRepository.fullTextSearch(keyword, pageable);
        } else {
            paging = boardRepository.findByTitleContaining(keyword, pageable);
        }
        return paging.map(board -> new BoardListDTO(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getViewCount(),
                anonymous(board.getId()),
                board.getCreatedDate(),
                board.getModifiedDate()
        ));
    }
    //익명 체크 했을 경우 익명으로 표시
    public String anonymous(Long postId) {
        Board board = boardRepository.findById(postId).orElseThrow(() ->
        new IllegalArgumentException("없는 내용입니다"));
        if(!board.isAnonymous()) {
            return board.getWriter().getProfileName();
        }else {
            return "익명";
        }

    }
    //리스트
    @Transactional(readOnly = true)
    public BoardListDTO boardDetails(@PathVariable Long postId) {
        Board board = boardRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("게시물이 삭제되었습니다."));
        board.increaseViewCount();
        return new BoardListDTO(postId,
                board.getTitle(), board.getContent(), board.getViewCount(),
                anonymous(board.getId()), board.getCreatedDate(),
                board.getModifiedDate());
    }
    //삭제
    public void deletePosting(Long postId, String user) {
        Board board = boardRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("이미 삭제된 게시글입니다."));
        if(board.getWriter().getProfileName().equals(user)) {
            boardRepository.delete(board);
        }
    }

    //수정
    public void editPosting(BoardEditDTO boardEditDTO, Long postId, String user){
        Board board = boardRepository.findById(postId).orElseThrow(() ->
                new IllegalArgumentException("삭제된 게시글입니다."));
        if(board.getWriter().getUserID().equals(user)) {
            board.editPost(boardEditDTO);
        }
    }

}
