package com.example.girauser.controller;

import com.example.girauser.dto.MailDto;
import com.example.girauser.dto.TeamDto;
import com.example.girauser.dto.UserDto;
import com.example.girauser.service.TeamService;
import com.example.girauser.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final TeamService teamService;

    // 유저 관련 부분들

    //로그인
    @GetMapping("/signin")
    public ResponseEntity<?> signIn(HttpServletResponse response, @RequestBody UserDto dto ) {
        try {
            userService.signIn(dto, response);
            return ResponseEntity.ok().body("Sign in successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto dto) {
        try {
            userService.signUp(dto);
            return ResponseEntity.ok().body("Sign up successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //회원정보수정
    @PutMapping("/modify")
    public ResponseEntity<?> modify(@RequestBody UserDto dto, HttpServletRequest request) {
        try{
            userService.modify(dto, request);
            return ResponseEntity.ok().body("Modify successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //회원탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(HttpServletRequest request) {
        try {
            userService.delete(request);
            return ResponseEntity.ok().body("Delete successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //액세스 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> email, HttpServletResponse response) {
        try {
            userService.refresh(email, response);
            return ResponseEntity.ok().body("Refresh successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //회원 리스트 조회(관리자 전용)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/userlist")
    public ResponseEntity<?> userList() {
        try {
            return ResponseEntity.ok().body(userService.userList());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 팀 관련된 부분

    //조장이 팀을 만드는 것
    @PostMapping("/maketeam")
    public ResponseEntity<?> makeTeam(@RequestBody TeamDto dto, HttpServletResponse response) {
        try{
        teamService.makeTeam(dto, response);
        return ResponseEntity.ok().body("Make team successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //조장이 팀원을 초대하는 것(이메일로 링크 발송)
    @PostMapping("/inviteteam")
    public ResponseEntity<?> inviteTeam(@RequestBody MailDto dto, HttpServletRequest request) {
        try{
        teamService.inviteTeam(dto, request);
        return ResponseEntity.ok().body("Invite team successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //팀원이 초대를 수락하고 팀에 가입하는 것
    @PostMapping("/jointeam")
    public ResponseEntity<?> joinTeam(@RequestParam Map<String, String> params) {
        try{
            teamService.joinTeam(params);
            return ResponseEntity.ok().body("Join team successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //조장이 팀원을 삭제하는 것
    @DeleteMapping("/deleteteam")
    public ResponseEntity<?> deleteTeam(@RequestBody TeamDto dto, HttpServletRequest request) {
        try{
            teamService.deleteTeam(dto, request);
            return ResponseEntity.ok().body("Delete team successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //자신이 속한 팀의 리스트 반환
    @GetMapping("/teamlist")
    public ResponseEntity<?> teamList(HttpServletRequest request) {
        try{
            return ResponseEntity.ok().body(teamService.teamList(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
