package com.capstone.CapstoneProject.AI.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FrontResponseDTO {
    private String role;
    private String summary;
    @JsonProperty("role_icon")
    private String roleIcon;
    private List<String> roadmap;
    private List<ReasonItem> reasons;
    private List<StackItem> stacks;

    @Data
    @AllArgsConstructor
    public static class StackItem {
        String name;
        String category;
        String icon;
    }

    @Data
    @AllArgsConstructor
    public static class ReasonItem {
        String stack;
        String reason;
    }
}
