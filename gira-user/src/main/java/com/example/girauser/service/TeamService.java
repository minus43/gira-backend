package com.example.girauser.service;

import com.example.girauser.dto.MailDto;
import com.example.girauser.dto.TeamDto;
import com.example.girauser.entity.Team;
import com.example.girauser.entity.User;
import com.example.girauser.repository.TeamRepository;
import com.example.girauser.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MailSenderService mailSenderService;

    public Team makeTeam(TeamDto dto, HttpServletResponse response) {
        log.info("makeTeam");
        //기존에 생성된 팀이름과 중복 검증
        if(teamRepository.findByTeamName(dto.getTeamName()).isPresent()){
            throw new IllegalArgumentException("Team already exists");
        }
        Team leader = Team.builder()
                .teamName(dto.getTeamName())
                .userName(dto.getUserName())
                .userRole("LEADER")
                .build();
        teamRepository.save(leader);

        response.addHeader("team", leader.getTeamName());
        response.addHeader("memberRole", leader.getUserRole());
        return leader;
    }

    public void inviteTeam(MailDto dto, HttpServletRequest request) {
        log.info("inviteTeam");
        // 해당 로직을 호출하는 사람이 해당 팀의 리더가 맞는지 검증
        User leader = userRepository.findByEmail(request.getHeader("email")).orElseThrow(()->
                 new IllegalArgumentException("user not found")
                );
        Team team = teamRepository.findByTeamNameAndUserName(dto.getTeamName(), leader.getNickName()).orElseThrow(()->
                new IllegalArgumentException("team not found"));

        //초대하려는 사람이 이미 서비스에 가입한 사람인지 검사
        if(userRepository.findByEmail(dto.getAddress()).isPresent()){
            //팀에 가입된 사람이라면 팀에 합류하는 동작만 하는 링크만 보내준다
            dto.setTitle(team.getTeamName()+"에서 당신을 초대했습니다");
            dto.setContent("링크를 누르면 팀에 합류합니다."+"\n"
                    +"팀가입 링크: "
            );
            mailSenderService.sendSimpleMail(dto);
        } else {
            //팀에 가입되지 않은 사람이라면 먼저 회원가입을 유도하고 팀에 합류하도록 한다.
            dto.setTitle(team.getTeamName()+"에서 당신을 초대했습니다만");
            dto.setContent("당신은 아직 gira의 회원이 아닙니다!"+"\n"
                    +"1. 먼저 아래 링크를 따라 회원가입을 진행해주세요!"+"\n"
                    +"회원가입 링크: "+"리액트단 링크??"+"\n"
                    +"2. 회원가입을 완료하셨다면 아래 링크를 클릭해주세요!"+"\n"
                    +"팀가입 링크: "
            );
            mailSenderService.sendSimpleMail(dto);
        }
    }

    public Team joinTeam(Map<String, String> params) {
        log.info("joinTeam");
        //초대를 승인한 유저, 팀이 존재하는지 검증
        Team team = teamRepository.findByTeamName(params.get("teamName")).orElseThrow(()->
                new IllegalArgumentException("team not found"));
        User user = userRepository.findByEmail(params.get("userEmail")).orElseThrow(()->
                new IllegalArgumentException("user not found"));

        Team common = Team.builder()
                .teamName(team.getTeamName())
                .userName(user.getNickName())
                .userRole("COMMON")
                .build();
        teamRepository.save(common);
        return common;
    }

    public Team deleteTeam(TeamDto dto, HttpServletRequest request){
        log.info("deleteTeam");
        log.info(request.getHeader("email"));
        log.info(dto.toString());
        // 해당 로직을 호출하는 사람이 해당 팀의 리더가 맞는지 검증
        User leader = userRepository.findByEmail(request.getHeader("email")).orElseThrow(()->
                new IllegalArgumentException("user not found"));
        Team team = teamRepository.findByTeamNameAndUserName(dto.getTeamName(), leader.getNickName()).orElseThrow(()->
                new IllegalArgumentException("team not found"));

        //dto 안에 있는 해당 유저를 팀에서 제거
        //삭제하려는 유저가 존재하는지 검증
        User user = userRepository.findByNickName(dto.getUserName()).orElseThrow(()->
                new IllegalArgumentException("user not found"));
        Team common = teamRepository.findByTeamNameAndUserName(dto.getTeamName(), user.getNickName()).orElseThrow(()->
                new IllegalArgumentException("team not found"));

        teamRepository.delete(common);
        return common;
    }

    //자신이 속해있는 팀 리스트(팀명, 유저명, 유저역할)를 반환
    public List<TeamDto> teamList(HttpServletRequest request){
        log.info("teamList");
        User user = userRepository.findByEmail(request.getHeader("email")).orElseThrow(()->
                new IllegalArgumentException("user not found"));
        List<Team> teamList = teamRepository.findAllByUserName(user.getNickName());
        List<TeamDto> dtos =  new ArrayList<>();

        teamList.forEach(team -> {
            dtos.add(
                    TeamDto.builder()
                            .teamName(team.getTeamName())
                            .userName(team.getUserName())
                            .userRole(team.getUserRole())
                            .build()
            );
        });
        return dtos;
    }

    public List<TeamDto> totalTeamList() {
        log.info("totalTeamList");
        List<Team> teamList = teamRepository.findAll();
        List<TeamDto> dtos =  new ArrayList<>();
        teamList.forEach(team -> {
            dtos.add(
                    TeamDto.builder()
                            .teamName(team.getTeamName())
                            .userName(team.getUserName())
                            .userRole(team.getUserRole())
                            .build()
            );
        });
        return dtos;
    }

    //
}
