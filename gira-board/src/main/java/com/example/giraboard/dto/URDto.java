package com.example.giraboard.dto;

import com.example.giraboard.entity.UR;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class URDto {
    private String display;
    private String name;
    private String content;
    private LocalDateTime deadline;
    private String manager;
    private String writer;
    private String teamName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
