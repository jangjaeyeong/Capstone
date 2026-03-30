package com.capstone.CapstoneProject.Community.Free.Entity;

import com.capstone.CapstoneProject.Community.Post;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
public class FreeBoard extends Post {
}
