package com.example.girauser.entity;

import jakarta.persistence.*;
import jakarta.ws.rs.DefaultValue;
import lombok.*;

@Entity
@Table(name = "tbl_team")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="team_name", nullable = false, unique = true)
    private String teamName;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_role", nullable = false)
    @Builder.Default
    private String userRole="COMMON";
}
