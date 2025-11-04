package com.capstone.CapstoneProject.Member;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;//번호
    private String name; //이름
    @Column(name = "userID", unique = true)
    private String userID; //ID
    private  String password; //비밀번호
    @Column(name = "cellphone", unique = true)
    private String cellphone; //전화번호
    @Column(name = "profileName", unique = true)
    private String profileName; //닉네임

    @ManyToMany(fetch = FetchType.LAZY) // 성능을 위해 LAZY로 설정
    @JoinTable(
            name = "member_authority",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();
}
