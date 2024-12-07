package com.example.giraboard.repository;

import com.example.giraboard.entity.FE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FERepositoy extends JpaRepository<FE, Long> {
    void deleteAllByTeamName(String teamName);

    Optional<FE> findByTeamName(String teamName);
}
