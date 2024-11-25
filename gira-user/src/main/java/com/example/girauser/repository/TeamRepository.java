package com.example.girauser.repository;

import com.example.girauser.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByUserName(String email);

    Optional<Team> findByTeamName(String teamName);

    Optional<Team> findByTeamNameAndUserName(String teamName, String userName);

    List<Team> findAllByUserName(String nickName);
}
