package com.example.girauser.dto;

import lombok.*;

@Setter@Getter@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResDto {
    private String email;
    private String nickName;
}
