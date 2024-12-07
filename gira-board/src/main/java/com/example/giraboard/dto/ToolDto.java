package com.example.giraboard.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolDto {
    private String type;
    private String name;
    private String version;
    private String writer;
    private String teamName;
    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
