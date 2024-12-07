package com.example.giraboard.service;

import com.example.giraboard.dto.*;
import com.example.giraboard.entity.*;
import com.example.giraboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final ThemeRepositoy themeRepositoy;
    private final ToolRepository toolRepository;
    private final URRepositoy urRepositoy;
    private final BERepositoy beRepositoy;
    private final FERepositoy feRepositoy;
    
    public void makeBoard(Map<String, String> team) {
        String teamName=team.get("teamName");
        Theme theme = Theme.builder()
                .teamName(teamName)
                .build();
        themeRepositoy.save(theme);
        
        Tool tool = Tool.builder()
                .teamName(teamName)
                .build();
        toolRepository.save(tool);
        
        UR ur = UR.builder()
                .teamName(teamName)
                .build();
        urRepositoy.save(ur);
        
        BE be = BE.builder()
                .teamName(teamName)
                .build();
        beRepositoy.save(be);
        
        FE fe = FE.builder()
                .teamName(teamName)
                .build();
        feRepositoy.save(fe);
        
    }

    public void deleteBoard(Map<String, String> team) {
        String teamName=team.get("teamName");
        themeRepositoy.deleteAllByTeamName(teamName);
        toolRepository.deleteAllByTeamName(teamName);
        urRepositoy.deleteAllByTeamName(teamName);
        beRepositoy.deleteAllByTeamName(teamName);
        feRepositoy.deleteAllByTeamName(teamName);
    }

    public Theme updateTheme(ThemeDto dto) {
        Theme theme = themeRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        theme.setTitle(dto.getTitle());
        theme.setContent(dto.getContent());
        theme.setWriter(dto.getWriter());
        themeRepositoy.save(theme);
        return theme;
    }

    public void updateTool(Map<String, List<Tool>> toolList) {
        List<Tool> addTool = toolList.get("addTool");
        log.info(addTool.toString());
        if(!addTool.isEmpty()) toolRepository.saveAll(addTool);
        List<Tool> removeTool = toolList.get("removeTool");
        if(!removeTool.isEmpty())toolRepository.deleteAll(removeTool);
        List<Tool> editTool = toolList.get("editTool");
        if(!editTool.isEmpty())toolRepository.saveAll(editTool);
    }

    public void updateUR(Map<String, List<UR>> urList) {
        List<UR> addUR = urList.get("addUR");
        if(!addUR.isEmpty())urRepositoy.saveAll(addUR);
        List<UR> removeUR = urList.get("removeUR");
        if(!removeUR.isEmpty())urRepositoy.deleteAll(removeUR);
        List<UR> editUR = urList.get("editUR");
        if(!editUR.isEmpty())urRepositoy.saveAll(editUR);
    }

    public BE updateBE(BEDto dto) {
        BE be = beRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        be.setApi(dto.getApi());
        be.setErd(dto.getErd());
        be.setWriter(dto.getWriter());
        beRepositoy.save(be);
        return be;
    }

    public FE updateFE(FEDto dto) {
        FE fe = feRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        fe.setWireframe(dto.getWireframe());
        fe.setWriter(dto.getWriter());
        feRepositoy.save(fe);
        return fe;
    }

    public ThemeDto getTheme(Map<String, String> team) {
        Theme theme = themeRepositoy.findByTeamName(team.get("teamName")).orElseThrow();
        return theme.toDto();
    }

    public List<Tool> getTool(Map<String, String> team) {
        return toolRepository.findAllByTeamName(team.get("teamName"));
    }

    public List<UR> getUR(Map<String, String> team) {
        return urRepositoy.findAllByTeamName(team.get("teamName"));
    }

    public BEDto getBE(Map<String, String> team) {
        BE be = beRepositoy.findByTeamName(team.get("teamName")).orElseThrow();
        return be.toDto();
    }

    public FEDto getFE(Map<String, String> team) {
        FE fe = feRepositoy.findByTeamName(team.get("teamName")).orElseThrow();
        return fe.toDto();
    }
}
