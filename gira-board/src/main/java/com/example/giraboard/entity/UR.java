package com.example.giraboard.entity;

import com.example.giraboard.dto.URDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Entity
@Table(name = "tbl_ur")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UR {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "display")
    String display;
    @Column(name = "name")
    String name;
    @Column(name = "content")
    String content;
    @Column(name = "deadline")
    LocalDateTime deadline;
    @Column(name = "manager")
    String manager;

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

    public URDto toDto() {
        return URDto.builder()
                .display(display)
                .name(name)
                .content(content)
                .deadline(deadline)
                .manager(manager)
                .modDate(modDate)
                .regDate(regDate)
                .writer(writer)
                .build();
    }
}
