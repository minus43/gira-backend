package com.example.giraboard.repository;

import com.example.giraboard.entity.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThemeRepositoy extends JpaRepository<Theme, Long> {
    void deleteAllByTeamName(String team);

    Optional<Theme> findByTeamName(String teamName);
}
