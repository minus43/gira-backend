package com.example.girauser.dto;


import lombok.*;

@Setter@Getter@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MailDto {
    private String teamName;
    private String address;
    private String title;
    private String content;

}
