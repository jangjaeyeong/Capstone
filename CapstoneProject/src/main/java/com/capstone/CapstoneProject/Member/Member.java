package com.capstone.CapstoneProject.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;//번호
    private String name; //이름
    @Column(name = "userID", unique = true)
    private String userID; //ID
    private  String password; //비밀번호
    @Column(name = "cellphone", unique = true)
    private String cellphone; //전화번호
    @Column(name = "profileName", unique = true)
    private String profileName; //닉네임

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL) // 성능을 위해 LAZY로 설정
    @JoinTable(
            name = "member_authority",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id")
    )

    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();
}
