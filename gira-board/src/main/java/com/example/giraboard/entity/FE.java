package com.example.giraboard.entity;

import com.example.giraboard.dto.FEDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_fe")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "wireframe")
    String wireframe;

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

    public FEDto toDto(){
        return FEDto.builder()
                .wireframe(wireframe)
                .modDate(modDate)
                .regDate(regDate)
                .writer(writer)
                .build();
    }
}
