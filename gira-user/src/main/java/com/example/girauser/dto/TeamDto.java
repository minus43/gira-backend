package com.example.girauser.dto;

import lombok.*;

@Setter@Getter@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamDto {
    private String teamName;
    private String userName;
    private String userRole;
}
