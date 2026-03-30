package com.capstone.CapstoneProject.AI.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tools {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "toolName", unique = true)
    private String toolName;
    @Column(name = "comment")
    private String comment;
    @Column(name = "toolType")
    private String ToolType;
}
