package com.example.giraboard.controller;


import com.example.giraboard.common.response.CommonResDto;
import com.example.giraboard.dto.*;
import com.example.giraboard.entity.*;
import com.example.giraboard.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    //보드 생성
    @PostMapping("/makeboard")
    public ResponseEntity<?> makeBoard(@RequestBody Map<String, String> team) {
        boardService.makeBoard(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED,"보드 생성 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    //보드 삭제
    @DeleteMapping("/deleteboard")
    public ResponseEntity<?> deleteBoard(@RequestBody Map<String, String> team) {
        boardService.deleteBoard(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"보드 삭제 완료",null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //수정
    @PutMapping("/updatetheme")
    public ResponseEntity<?> updateTheme(@RequestBody ThemeDto dto) {
        Theme theme = boardService.updateTheme(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"주제 수정 완료",theme);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatetool")
    public ResponseEntity<?> updateTool(@RequestBody Map<String,List<Tool>> toolList) {
        boardService.updateTool(toolList);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"도구 수정 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updateur")
    public ResponseEntity<?> updateUR(@RequestBody Map<String,List<UR>> urList) {
        boardService.updateUR(urList);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"요구사항 & WBS 수정 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatebe")
    public ResponseEntity<?> updateBE(@RequestBody BEDto dto) {
        BE be = boardService.updateBE(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"벡엔드 수정 완료", be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatefe")
    public ResponseEntity<?> updateFE(@RequestBody FEDto dto) {
        FE fe = boardService.updateFE(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"프론트엔드 수정 완료", fe);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //조회
    @GetMapping("/gettheme")
    public ResponseEntity<?> getTheme(@RequestBody Map<String, String> team) {
        ThemeDto theme = boardService.getTheme(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "주제 조회 완료", theme);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/gettool")
    public ResponseEntity<?> getTool(@RequestBody Map<String, String> team) {
        List<Tool> toolList = boardService.getTool(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"툴 리스트 조회 완료", toolList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getur")
    public ResponseEntity<?> getUR(@RequestBody Map<String, String> team) {
        List<UR> urList = boardService.getUR(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "요수사항&WBS 리스트 조회 완료", urList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getbe")
    public ResponseEntity<?> getBE(@RequestBody Map<String, String> team) {
        BEDto be = boardService.getBE(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"벡엔드 조회 완료",be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getfe")
    public ResponseEntity<?> getFE(@RequestBody Map<String, String> team) {
        FEDto fe = boardService.getFE(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"프론트엔드 조회 완료",fe);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
