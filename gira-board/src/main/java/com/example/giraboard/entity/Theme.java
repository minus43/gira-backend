package com.example.giraboard.entity;

import com.example.giraboard.dto.ThemeDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "tbl_theme")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name ="title")
    String title;
    @Column(name = "content")
    String content;

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

    public ThemeDto toDto() {
        return ThemeDto.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .modDate(modDate)
                .regDate(regDate)
                .build();
    }
}
