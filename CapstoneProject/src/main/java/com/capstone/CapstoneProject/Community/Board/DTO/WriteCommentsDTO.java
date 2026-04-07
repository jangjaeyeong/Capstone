package com.capstone.CapstoneProject.Community.Board.DTO;

import com.capstone.CapstoneProject.Community.Board.Entity.Board;
import com.capstone.CapstoneProject.Community.Board.Entity.Comments;
import com.capstone.CapstoneProject.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.parameters.P;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WriteCommentsDTO {
    private String content;
    private boolean anonymous;

      public Comments toEntity(Member writer, Board boardId) {
          return Comments.builder()
                  .comment(this.content)
                  .anonymous(this.anonymous)
                  .writer(writer)
                  .postId(boardId)
                  .build();
      }
}
