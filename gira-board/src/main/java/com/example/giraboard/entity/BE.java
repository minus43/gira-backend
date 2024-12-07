package com.example.giraboard.entity;

import com.example.giraboard.dto.BEDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_be")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BE {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name = "erd")
    String erd;
    @Column(name = "api")
    String api;

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

    public BEDto toDto(){
        return BEDto.builder()
                .erd(erd)
                .api(api)
                .modDate(modDate)
                .regDate(regDate)
                .writer(writer)
                .build();
    }
}
