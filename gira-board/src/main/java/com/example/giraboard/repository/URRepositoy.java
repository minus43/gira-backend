package com.example.giraboard.repository;

import com.example.giraboard.entity.UR;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface URRepositoy extends JpaRepository<UR, Long> {
    void deleteAllByTeamName(String teamName);

    Optional<UR> findByTeamName(String teamName);

    List<UR> findAllByTeamName(String teamName);
}
