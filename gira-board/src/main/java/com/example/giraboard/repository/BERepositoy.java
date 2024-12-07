package com.example.giraboard.repository;

import com.example.giraboard.entity.BE;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface BERepositoy extends JpaRepository<BE, Long> {
    void deleteAllByTeamName(String teamName);

    Optional<BE> findByTeamName(String teamName);
}
