package com.example.giraboard.entity;

import com.example.giraboard.dto.ToolDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "tbl_tool")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tool {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "type")
    String type;
    @Column(name = "name")
    String name;
    @Column(name = "version")
    String version;

    @Column(name = "team_name", nullable = false)
    String teamName;
    @Column(name = "writer")
    String writer;
    @Column(name = "reg_date")
    @CreationTimestamp
    LocalDateTime regDate;
    @Column(name = "mod_date")
    @UpdateTimestamp
    LocalDateTime modDate;

    public ToolDto toDto() {
        return ToolDto.builder()
                .type(type)
                .name(name)
                .version(version)
                .modDate(modDate)
                .regDate(regDate)
                .writer(writer)
                .build();
    }
}
