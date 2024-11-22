package com.example.girauser.dto;

import com.example.girauser.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter@Getter@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String email;
    private String password;
    private String nickName;

    public User toEntity(PasswordEncoder encoder) {
        return User.builder()
                .email(email)
                .password(encoder.encode(password))
                .nickName(nickName)
                .build();
    }
}
