package com.example.giraboard.repository;

import com.example.giraboard.entity.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Long> {
    void deleteAllByTeamName(String teamName);

    Optional<Tool> findByTeamName(String teamName);

    List<Tool> findAllByTeamName(String teamName);
}
