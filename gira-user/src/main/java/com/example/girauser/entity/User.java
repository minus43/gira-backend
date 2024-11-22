package com.example.girauser.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user")
@Getter@Setter@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "nick_name", unique = true, nullable = false)
    private String nickName;

    @Column(name ="role", nullable = false)
    @Builder.Default
    private String role = "USER";

}
