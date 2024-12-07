package com.example.giraboard.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter@Setter@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThemeDto {
    private String title;
    private String content;
    private String writer;
    private String teamName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
