package com.example.girauser.controller;

import com.example.girauser.common.response.CommonResDto;
import com.example.girauser.dto.MailDto;
import com.example.girauser.dto.TeamDto;
import com.example.girauser.dto.UserDto;
import com.example.girauser.dto.UserResDto;
import com.example.girauser.entity.Team;
import com.example.girauser.entity.User;
import com.example.girauser.service.TeamService;
import com.example.girauser.service.UserService;
import com.example.girauser.util.BoardFeignClient;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final TeamService teamService;
    private final BoardFeignClient boardFeignClient;

    // 유저 관련 부분들

    //로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(HttpServletResponse response, @RequestBody UserDto dto ) {

        HttpServletResponse result = userService.signIn(dto, response);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK, "로그인 완료", result.getHeader("Authorization"));
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto dto) {
        User user = userService.signUp(dto);
        CommonResDto resDto = new CommonResDto(HttpStatus.CREATED, "회원가입 완료", user.getId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    //회원정보수정
    @PutMapping("/modifyuser")
    public ResponseEntity<?> modify(@RequestBody UserDto dto, HttpServletRequest request) {
        User user = userService.modify(dto, request);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "회원정보 수정 완료", user.getId());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    //회원탈퇴
    @DeleteMapping("/deleteuser")
    public ResponseEntity<?> delete(HttpServletRequest request) {
        userService.delete(request);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"회원 탈퇴 완료", null);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    //액세스 토큰 갱신
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> email, HttpServletResponse response) {

        HttpServletResponse result = userService.refresh(email, response);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "토큰 갱신 완료", result.getHeader("Authorization"));
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }


    //회원 리스트 조회(관리자 전용)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/userlist")
    public ResponseEntity<?> userList() {

        List<UserResDto> userResDtos = userService.userList();
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"유저 리스트 조회 완료", userResDtos);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    // 팀 관련된 부분

    //조장이 팀을 만드는 것
    @PostMapping("/maketeam")
    public ResponseEntity<?> makeTeam(@RequestBody TeamDto dto, HttpServletResponse response) {
        Team leader = teamService.makeTeam(dto, response);
        Map<String,String> team= new HashMap<>();
        team.put("teamName",leader.getTeamName());
        ResponseEntity<?> makeBoard = boardFeignClient.makeBoard(team);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED, "팀 생성 완료", leader.getTeamName()+" "+leader.getUserName()+" "+leader.getUserRole()+" "+makeBoard.getBody());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    //조장이 팀원을 초대하는 것(이메일로 링크 발송)
    @PostMapping("/inviteteam")
    public ResponseEntity<?> inviteTeam(@RequestBody MailDto dto, HttpServletRequest request) {
        teamService.inviteTeam(dto, request);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"팀원에게 초대 메일 발송", null);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    //팀원이 초대를 수락하고 팀에 가입하는 것
    @PostMapping("/jointeam")
    public ResponseEntity<?> joinTeam(@RequestParam Map<String, String> params) {
        Team common = teamService.joinTeam(params);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.CREATED,"팀원 가입 완료", common.getTeamName()+common.getUserName()+common.getUserRole());
        return new ResponseEntity<>(resDto, HttpStatus.CREATED);
    }

    //조장이 팀원을 삭제하는 것
    @DeleteMapping("/deleteteam")
    public ResponseEntity<?> deleteTeam(@RequestBody TeamDto dto, HttpServletRequest request) {
        Team common = teamService.deleteTeam(dto, request);
        if(common.getUserRole().equals("LEADER")){
            Map<String,String> team= new HashMap<>();
            team.put("teamName",common.getTeamName());
            ResponseEntity<?> deleteBoard = boardFeignClient.deleteBoard(team);
            CommonResDto resDto = new CommonResDto(HttpStatus.OK,"팀 삭제 완료", null);
            return new ResponseEntity<>(resDto, HttpStatus.OK);
        }
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"팀원 삭제 완료", common.getTeamName()+common.getUserName()+common.getUserRole());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    //자신이 속한 팀의 리스트 반환
    @GetMapping("/teamlist")
    public ResponseEntity<?> teamList(HttpServletRequest request) {
        List<TeamDto> teamDtos = teamService.teamList(request);
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"유저가 속한 팀 조회 완료",teamDtos);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    //전체 팀 조회(관리자용)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/totalteamlist")
    public ResponseEntity<?> totalTeamList(HttpServletRequest request) {
        List<TeamDto> teamDtos = teamService.totalTeamList();
        CommonResDto resDto
                = new CommonResDto(HttpStatus.OK,"전체 팀원 조회 완료", teamDtos);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}
